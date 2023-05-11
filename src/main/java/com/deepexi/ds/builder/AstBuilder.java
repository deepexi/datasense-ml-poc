package com.deepexi.ds.builder;

import static com.deepexi.ds.ast.expression.ArithmeticExpression.ArithmeticOperator.ADD;
import static com.deepexi.ds.ast.expression.ArithmeticExpression.ArithmeticOperator.MUL;
import static com.deepexi.ds.ast.expression.UdfExpression.BASE_DATE_19700101;
import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.DateTimeUnit;
import com.deepexi.ds.ast.MetricQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.OrderBy.OrderByDirection;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.ArithmeticExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.UdfExpression;
import com.deepexi.ds.ast.expression.UdfExtractDatePart;
import com.deepexi.ds.ast.window.FrameBoundary;
import com.deepexi.ds.ast.window.FrameBoundaryBase;
import com.deepexi.ds.ast.window.FrameType;
import com.deepexi.ds.ast.window.Window;
import com.deepexi.ds.astbuilder.model.ModelBuilder;
import com.deepexi.ds.builder.express.ColumnNameRewriter;
import com.deepexi.ds.builder.express.ColumnTableNameAdder;
import com.deepexi.ds.builder.express.ColumnTableNameReplacer;
import com.deepexi.ds.builder.express.MetricExpressionParser;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlMetricQuery.YmlOrderBy;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.YmlWindow;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unchecked")
public class AstBuilder {

  private YmlMetricQuery metricQuery;

  // 需要查询的指标
  private List<YmlMetric> metrics;

  // 关联的 model, 如果一个孤立的model 不被引用, 将不会出现在此集合中
  private Model model4Metrics;

  public AstBuilder(YmlFullQuery ymlFullQuery) {
    parseAndCheckIntegrity(ymlFullQuery);
  }

  public Relation build() {
    Relation query = metricsOnSameModel();
    if (query == null) {
      throw new TODOException("目前仅支持针对同一个model的多指标查询");
    }
    return query;
  }

  private void parseAndCheckIntegrity(YmlFullQuery ymlFullQuery) {
    this.metricQuery = ymlFullQuery.getQuery();
    if (metricQuery == null) {
      throw new ModelException("metric query must not be null");
    }

    // 查找 YmlMetric, 并解析其依赖的 Model
    Map<String, YmlMetric> existMetrics = ymlFullQuery.getMetrics().stream()
        .collect(Collectors.toMap(YmlMetric::getName, Function.identity()));
    metrics = new ArrayList<>();
    metricQuery.getMetricNames().forEach(m -> {
      YmlMetric element = existMetrics.get(m);
      if (element == null) {
        throw new ModelException("miss metric_def [%s]");
      }
      metrics.add(element);
    });
    if (metrics.size() == 0) {
      throw new ModelException("at least one metric at query");
    }

    ImmutableList<YmlModel> ymlModels = ymlFullQuery.getModels();
    Set<String> minDims = new HashSet<>(this.metricQuery.getDimensions());
    for (YmlMetric ele : this.metrics) {
      // 所有 metrics.dimension 必定包含 query.dimension
      boolean metricHasAllDims = ele.getDimensions().containsAll(minDims);
      if (!metricHasAllDims) {
        throw new ModelException(String.format("query[%s] 包含的维度 超过 metric=[%s]的预设范围",
            this.metricQuery.getName(), ele.getName()));
      }
      String requiredModelName = ele.getModelName();
      if (model4Metrics != null) {
        if (!(model4Metrics.getName().getValue().equals(requiredModelName))) {
          throw new ModelException("目前仅支持 同一个model的指标");
        } else {
          continue;
        }
      }

      model4Metrics = ModelBuilder.singleTreeModel(ymlModels);
    }
    if (metrics.size() == 0) {
      throw new ModelException("found no model_def");
    }
  }

  /**
   * 针对一个 model 的多指标查询
   */
  private Relation metricsOnSameModel() {
    if (model4Metrics == null) {
      return null;
    }

    // all metrics must against SAME root model
    boolean allSameModel = this.metrics.stream()
        .allMatch(m -> Objects.equals(m.getModelName(), model4Metrics.getName().getValue()));
    if (!allSameModel) {
      return null;
    }

    // collect all metrics
    List<Column> metricsCol = new ArrayList<>();
    ColumnTableNameAdder tableNameAdder = new ColumnTableNameAdder(model4Metrics);
    for (YmlMetric m : this.metrics) {
      String alias = m.getName();
      Expression expression = ParserUtils.parseStandaloneExpression(m.getAggregate());
      ColumnDataType dataType = ColumnDataType.fromName(m.getDataType());
      Column rawCol = new Column(alias, expression, dataType, null, null);
      Column column = (Column) tableNameAdder.process(rawCol);
      metricsCol.add(column);
    }
    // name
    Identifier metricQueryName = Identifier.of(metricQuery.getName());

    // dimension的处理策略: 取 YmlMetricQuery.query对象中的 dimension
    final List<Column> dimensions = new ArrayList<>();
    metricQuery.getDimensions().forEach((String dimName) -> {
      // 该维度在 metric对应的 model上
      Column dimOfModel = model4Metrics.getDimensions().stream()
          .filter(c -> c.getAlias().equals(dimName))
          .findAny()
          .orElse(null);
      if (dimOfModel == null) {
        throw new ModelException(
            String.format("dim [%s] not found in columns of model[%s]", dimName,
                model4Metrics.getName().getValue()));
      }
      Column dim = new Column(
          dimOfModel.getAlias(),
          new Identifier(model4Metrics.getName().getValue(), dimOfModel.getAlias()),
          dimOfModel.getDataType(),
          dimOfModel.getDatePart(),
          null
      );
      dimensions.add(dim);
    });

    // metricFilters
    final List<Expression> metricFilters = new ArrayList<>();
    metricQuery.getMetricFilters().forEach((String metricFilter) -> {
      MetricExpressionParser parser = new MetricExpressionParser(metrics, metricFilter);
      Expression expr = parser.parse();
      metricFilters.add(expr);
    });

    // modelFilters
    final List<Expression> modelFilters = new ArrayList<>();
    metricQuery.getModelFilters().forEach((String filterStr) -> {
      Expression expr3 = ParserUtils.parseBooleanExpression(filterStr);
      modelFilters.add(expr3);
    });

    // orderBy limit offset
    List<OrderBy> orderBys = new ArrayList<>();
    metricQuery.getOrderBys().forEach((YmlOrderBy ymlOrderBy) -> {
      String colName = ymlOrderBy.getName();
      Identifier name1 = new Identifier(model4Metrics.getName().getValue(), colName);
      OrderByDirection direction1 = OrderByDirection.fromName(ymlOrderBy.getDirection());
      orderBys.add(new OrderBy(name1, direction1));
    });

    // columns = dimension + metrics
    List<Column> selectColumns = new ArrayList<>();
    selectColumns.addAll(dimensions);
    selectColumns.addAll(metricsCol);

    if (this.metricQuery.getWindow() == null) {
      return new MetricQuery(
          metricQueryName,    // id
          model4Metrics,      // from
          modelFilters,       // where
          dimensions,         // groupBy
          metricFilters,      // having
          selectColumns,      // select字段
          // orderBy limit offset
          orderBys,
          metricQuery.getLimit(),
          metricQuery.getOffset());
    }

    /*
     * 某些聚合函数不支持 group + window
     * sum(col)   => group时 sum(col) + window中 再次sum
     * count(col) => group时 count(col) + window中 再次sum
     * avg(col)
     * count(distinct col)
     */

    // 包含 window, 需要创建一个额外的 MetricBindQuery, 记做 midMetric
    // 在 midMetric完成 group by
    // 之上还有一层 MetricBindQuery, 完成窗口运算, 记做 upper

    // ======== 处理 midMetric
    String midLayerName = generateMidLayerAlias(metricQuery);
    Identifier midMetricId = Identifier.of(midLayerName);
    MetricQuery midMetric = new MetricQuery(
        midMetricId,    // id
        model4Metrics,  // relation
        modelFilters,   // where
        dimensions,     // groupBy
        metricFilters,  // having
        selectColumns,  // select字段
        // orderBy limit offset
        EMPTY_LIST,
        null,
        null);

    // window
    Window window = buildWindow(midMetric);

    // orderBys 处理
    ColumnTableNameReplacer tableNameReplacer = new ColumnTableNameReplacer(
        midMetric.getSource().getTableName(),
        midMetric.getName()
    );

    List<OrderBy> orderByUpper = new ArrayList<>(orderBys.size());
    orderBys.forEach(orderBy -> {
      Identifier identifier = (Identifier) tableNameReplacer.process(orderBy.getName());
      orderByUpper.add(new OrderBy(identifier, orderBy.getDirection()));
    });

    // column 处理
    List<Column> upperColumns = buildColumnForUpperModel(midMetric, metricsCol, window);

    // 最终返回一个 Model
    return new Model(
        metricQueryName,        // model id
        midMetric,              // from what relation
        EMPTY_LIST,             // join
        upperColumns,           // exposure columns
        EMPTY_LIST,             // dimensions
        orderByUpper,           // order by
        metricQuery.getLimit(), // limit
        metricQuery.getOffset() // offset
    );
  }

  private List<Column> buildColumnForUpperModel(MetricQuery midMetric,
      List<Column> metrics,
      Window window) {
    ColumnTableNameReplacer tableNameReplacer = new ColumnTableNameReplacer(
        midMetric.getSource().getTableName(),
        midMetric.getName()
    );

    // 指标维度列, 改写 tableName即可
    List<Column> upperColumns = new ArrayList<>();
    for (int i = 0; i < midMetric.getGroupBy().size(); i++) {
      Column dimInMid = midMetric.getGroupBy().get(i);
      Column column = (Column) tableNameReplacer.process(dimInMid);
      upperColumns.add(column);
    }

    // 指标列, 改tableName, 该字段名,  加window
    for (Column metricInMid : metrics) {
      // 改 tableName
      Column columnInToTable = (Column) tableNameReplacer.process(metricInMid);

      // 改 fieldName
      String toName = metricInMid.getAlias();
      ColumnNameRewriter columnNameRewriter = new ColumnNameRewriter(midMetric.getName(), toName);
      Column noWindow = (Column) columnNameRewriter.process(columnInToTable);

      // 添加 window 到该 column
      Column hasWindow = new Column(
          noWindow.getAlias(),
          noWindow.getExpr(),
          noWindow.getDataType(),
          noWindow.getDatePart(),
          window
      );
      upperColumns.add(hasWindow);
    }

    return upperColumns;
  }

  /**
   * 构建 window, 返回的window已经处理过, 基于 fromRelation
   */
  private Window buildWindow(MetricQuery fromRelation) {
    YmlWindow ymlWindow = this.metricQuery.getWindow();
    if (ymlWindow == null) {
      return null;
    }

    // 分析 trailing 的语义, 然后组装 window
    String trailing = ymlWindow.getTrailing().trim();
    String[] parts = trailing.split(" ");
    List<String> trailingInfo = Arrays.stream(parts)
        .filter(StringUtils::isNotEmpty).collect(
            Collectors.toList());

    if (trailingInfo.size() == 1) {
      // 按次数累计
      throw new ModelException("TODO");
    }
    if (trailingInfo.size() == 2) {
      DateTimeUnit dateTimeUnit = DateTimeUnit.fromName(trailingInfo.get(1));
      if (dateTimeUnit == null) {
        throw new ModelException("unrecognized datetime unit:" + trailingInfo.get(1));
      }
      String intOrUnbounded = trailingInfo.get(0);
      boolean unbounded = Objects.equals(intOrUnbounded, "unbounded");
      if (unbounded) {
        return buildWindowUnbounded(fromRelation, dateTimeUnit);
      } else {
        int intValue = Integer.parseInt(trailingInfo.get(0));
        return buildWindowRange(fromRelation, intValue, dateTimeUnit);
      }
    }
    throw new ModelException("not support");
  }

  private static Window buildWindowRange(MetricQuery from, int value, DateTimeUnit unit) {
    // partitions
    List<Identifier> partitions = EMPTY_LIST;
    if (from.getGroupBy().size() > 0) {
      partitions = new ArrayList<>(from.getGroupBy().size());
      for (int i = 0; i < from.getGroupBy().size(); i++) {
        Column dimCol = from.getGroupBy().get(i);
        boolean keepThisDimForPartition = (dimCol.getDatePart() == null);
        // 注意 >, 比如连续3月, 不保留 任何日期相关 partition
        if (keepThisDimForPartition) {
          partitions.add(new Identifier(from.getName().getValue(), dimCol.getAlias()));
        }
      }
    }

    // 把所有时间相关维度全部提取出来
    Column dateCol = null;
    Column dateTimeCol = null;
    Column timestampCol = null;
    Column yearCol = null;
    Column monthCol = null;
    Column dayCol = null;
    for (int i = 0; i < from.getGroupBy().size(); i++) {
      Column dimCol = from.getGroupBy().get(i);
      if (dimCol.getDatePart() == null) {
        continue;
      }
      switch (dimCol.getDatePart()) {
        case DATE:
          dateCol = dimCol;
          break;
        case DATETIME:
          dateTimeCol = dimCol;
          break;
        case TIMESTAMP:
          timestampCol = dimCol;
          break;
        case YEAR:
          yearCol = dimCol;
          break;
        case MONTH:
          monthCol = dimCol;
          break;
        case DAY:
          dayCol = dimCol;
          break;
        case HOUR:
        case MINUTE:
        case SECOND:
        default:
          break;
      }
    }

    // order by. 比如连续3月|天, 需要对日期进行int化, 然后进行
    List<OrderBy> orderBys = new ArrayList<>(1); // 日期

    if (unit == DateTimeUnit.YEAR) { // 年, 必须有可以 包含年信息的列
      if (yearCol != null) {
        Identifier orderByColId = new Identifier(from.getName().getValue(), yearCol.getAlias());
        orderBys.add(new OrderBy(orderByColId, OrderByDirection.ASC));
      }
      //
      else if (dateCol != null) {
        Identifier dateColId = new Identifier(from.getName().getValue(), dateCol.getAlias());
        UdfExtractDatePart getYear = new UdfExtractDatePart(dateColId, DateTimeUnit.DATE,
            DateTimeUnit.YEAR);
        orderBys.add(new OrderBy(getYear, OrderByDirection.ASC));
      }
      //
      else if (dateTimeCol != null) {
        Identifier dateColId = new Identifier(from.getName().getValue(), dateTimeCol.getAlias());
        UdfExtractDatePart getYear = new UdfExtractDatePart(dateColId, DateTimeUnit.DATETIME,
            DateTimeUnit.YEAR);
        orderBys.add(new OrderBy(getYear, OrderByDirection.ASC));
      }
      //
      else if (timestampCol != null) {
        Identifier dateColId = new Identifier(from.getName().getValue(), timestampCol.getAlias());
        UdfExtractDatePart getYear = new UdfExtractDatePart(dateColId, DateTimeUnit.TIMESTAMP,
            DateTimeUnit.YEAR);
        orderBys.add(new OrderBy(getYear, OrderByDirection.ASC));
      } else {
        throw new ModelException(
            "window year, but not provided column: year or datetime or date or timestamp");
      }
    }

    // 计算月份差
    else if (unit == DateTimeUnit.MONTH) {
      if (yearCol != null && monthCol != null) {
        Identifier yearColId = new Identifier(from.getName().getValue(), yearCol.getAlias());
        Identifier monthColId = new Identifier(from.getName().getValue(), monthCol.getAlias());
        // 12 * year
        ArithmeticExpression diffYear = new ArithmeticExpression(yearColId, IntegerLiteral.of(12),
            MUL);
        // 12 * year + month
        ArithmeticExpression diffMonth = new ArithmeticExpression(diffYear, monthColId, ADD);
        orderBys.add(new OrderBy(diffMonth, OrderByDirection.ASC));
      } else if (dateCol != null) {
        throw new TODOException();
      } else if (dateTimeCol != null) {
        throw new TODOException();
      } else if (timestampCol != null) {
        throw new TODOException();
      } else {
        throw new ModelException(
            "window year, but not provided column: year or datetime or date or timestamp");
      }
    }

    // 计算日期差
    else if (unit == DateTimeUnit.DAY) {
      if (yearCol != null && monthCol != null && dayCol != null) {
        Identifier yearColId = new Identifier(from.getName().getValue(), yearCol.getAlias());
        Identifier monthColId = new Identifier(from.getName().getValue(), monthCol.getAlias());
        Identifier dayColId = new Identifier(from.getName().getValue(), dayCol.getAlias());
        UdfExpression createDateByYMD = new UdfExpression("create_date_by_ymd",
            Arrays.asList(yearColId, monthColId, dayColId));
        UdfExpression dateDiff = new UdfExpression("date_diff", Arrays.asList(
            createDateByYMD, BASE_DATE_19700101));
        orderBys.add(new OrderBy(dateDiff, OrderByDirection.ASC));
      } else if (dateCol != null) {
        Identifier dateColId = new Identifier(from.getName().getValue(), dateCol.getAlias());
        UdfExpression dateDiff = new UdfExpression("date_diff", Arrays.asList(
            dateColId, BASE_DATE_19700101));
        orderBys.add(new OrderBy(dateDiff, OrderByDirection.ASC));
      } else if (dateTimeCol != null) {
        throw new TODOException();
      } else if (timestampCol != null) {
        throw new TODOException();
      } else {
        throw new ModelException(
            "window year, but not provided column: year or datetime or date or timestamp");
      }
    }

    // 比如 3天: 2 preceding ~ current row
    FrameBoundary start = new FrameBoundary(FrameBoundaryBase.N_PRECEDING, value - 1);
    FrameType frameType = FrameType.RANGE;
    FrameBoundary end = new FrameBoundary(FrameBoundaryBase.CURRENT_ROW, 0);
    return new Window(partitions, orderBys, frameType, start, end);
  }

  private static Window buildWindowUnbounded(MetricQuery from, DateTimeUnit unit) {
    // partitions
    List<Identifier> partitions = EMPTY_LIST;
    if (from.getGroupBy().size() > 0) {
      partitions = new ArrayList<>(from.getGroupBy().size());
      for (int i = 0; i < from.getGroupBy().size(); i++) {
        Column dimCol = from.getGroupBy().get(i);
        boolean keepThisDimForPartition = (dimCol.getDatePart() == null)
            || (dimCol.getDatePart().grainOrder >= unit.grainOrder);
        // 注意 >=, 比如月初至今, 需要保留 月作为 partition
        // 保留: 日期无关dim || 日期相关且粒度大于当前 unit
        if (keepThisDimForPartition) {
          partitions.add(new Identifier(from.getName().getValue(), dimCol.getAlias()));
        }
      }
    }

    // orderBys = 原dimension 中更细 1级 的时间粒度
    List<OrderBy> orderBys = EMPTY_LIST;
    if (partitions.size() > 0) {
      orderBys = new ArrayList<>(from.getGroupBy().size());
      for (int i = 0; i < from.getGroupBy().size(); i++) {
        Column dimCol = from.getGroupBy().get(i);
        boolean keepThisDimForOrderBy = (dimCol.getDatePart() != null)
            && ((dimCol.getDatePart().grainOrder + 1) == unit.grainOrder);
        if (keepThisDimForOrderBy) {
          Identifier orderByColId = new Identifier(from.getName().getValue(), dimCol.getAlias());
          orderBys.add(new OrderBy(orderByColId, OrderByDirection.ASC));
        }
      }
    }

    FrameBoundary start = new FrameBoundary(FrameBoundaryBase.UNBOUNDED_PRECEDING, 0);
    FrameType frameType = FrameType.ROWS;
    FrameBoundary end = new FrameBoundary(FrameBoundaryBase.CURRENT_ROW, 0);

    return new Window(partitions, orderBys, frameType, start, end);
  }

  private static String generateMidLayerAlias(YmlMetricQuery metricQuery) {
    // String midMetricName = "_mid_";
    String midMetricName = "_mid_" + metricQuery.getName();
    return midMetricName;
  }
}
