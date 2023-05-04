package com.deepexi.ds.builder;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.OrderBy.OrderByDirection;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.window.FrameBoundary;
import com.deepexi.ds.ast.window.FrameType;
import com.deepexi.ds.ast.window.Window;
import com.deepexi.ds.ast.window.FrameBoundaryBase;
import com.deepexi.ds.ast.window.WindowType;
import com.deepexi.ds.builder.express.BoolConditionParser;
import com.deepexi.ds.builder.express.ColumnNameRewriter;
import com.deepexi.ds.builder.express.ColumnTableNameRewriter;
import com.deepexi.ds.builder.express.MetricExpressionParser;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlFrameBoundary;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlMetricQuery.YmlOrderBy;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.YmlWindow;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class MetricBindQueryBuilder {

  private YmlMetricQuery metricQuery;

  // 需要查询的指标
  private Set<YmlMetric> metrics;

  // 关联的 model, 如果一个孤立的model 不被引用, 将不会出现在此集合中
  private Model model4Metrics;

  public MetricBindQueryBuilder(YmlFullQuery ymlFullQuery) {
    parseAndCheckIntegrity(ymlFullQuery);
  }

  public AstNode build() {
    AstNode query = metricsOnSameModel();
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
    metrics = new HashSet<>();
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
    Map<String, YmlModel> ymlModelsLookup = ymlModels.stream()
        .collect(Collectors.toMap(YmlModel::getName, Function.identity()));
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

      model4Metrics = new ModelBuilder(ymlModels, ymlModelsLookup.get(ele.getModelName())).build();
    }
    if (metrics.size() == 0) {
      throw new ModelException("found no model_def");
    }
  }

  /**
   * 针对一个 model 的多指标查询
   */
  private AstNode metricsOnSameModel() {
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
    List<Column> columns = new ArrayList<>();
    ColumnTableNameRewriter tableNameRewriter = new ColumnTableNameRewriter(
        model4Metrics.getName());
    for (YmlMetric m : this.metrics) {
      String alias = m.getName();
      Expression expression = ParserUtils.parseStandaloneExpression(m.getAggregate());
      ColumnDataType dataType = ColumnDataType.fromName(m.getDataType());
      Column rawCol = new Column(alias, expression, dataType);
      Column column = (Column) tableNameRewriter.process(rawCol);
      columns.add(column);
    }
    // name
    Identifier metricQueryName = Identifier.of(metricQuery.getName());

    // dimension的处理策略: 取 YmlMetricQuery.query对象中的 dimension
    final List<Column> dimensions = new ArrayList<>();
    metricQuery.getDimensions().forEach(d -> {
      // 解析维度, 该维度在 metric对应的 model上
      Column dim = model4Metrics.getDimensions().stream()
          .filter(c -> c.getAlias().equals(d))
          .findAny()
          .orElse(null);
      if (dim == null) {
        throw new ModelException(String.format("dim [%s] not found in columns of model[%s]", d,
            model4Metrics.getName().getValue()));
      }
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
    RelationMock srcRelation = RelationMock.fromMode(model4Metrics);
    metricQuery.getModelFilters().forEach((String filterStr) -> {
      Expression expr3 = new BoolConditionParser(filterStr, EMPTY_LIST, srcRelation).parse();
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

    if (this.metricQuery.getWindow() == null) {
      return new MetricBindQuery(
          metricQueryName,
          model4Metrics,
          metricFilters,
          dimensions,
          modelFilters,
          columns,
          orderBys,
          metricQuery.getLimit(),
          metricQuery.getOffset());
    }

    // 包含 window, 需要创建一个额外的 MetricBindQuery, 记做 midMetric
    // 在 midMetric完成 group by
    // 之上还有一层 MetricBindQuery, 完成窗口运算, 记做 upper

    // ======== 处理 midMetric
    String midMetricName = "_mid_";
    // String midMetricName = "_mid_" + metricQuery.getName() ;
    Identifier midMetricId = Identifier.of(midMetricName);
    MetricBindQuery midMetric = new MetricBindQuery(
        midMetricId,
        model4Metrics,
        metricFilters,
        dimensions,
        modelFilters,
        columns,
        EMPTY_LIST,
        null,
        null);

    // window
    Window window = buildWindow(midMetricId);

    // orderBys 处理
    ColumnTableNameRewriter tableNameReplacer = new ColumnTableNameRewriter(
        midMetric.getName(),
        midMetric.getRelation().getTableName(),
        midMetric.getName()
    );

    List<OrderBy> orderByUpper = new ArrayList<>(orderBys.size());
    orderBys.forEach(orderBy -> {
      Identifier identifier = (Identifier) tableNameReplacer.process(orderBy.getName());
      orderByUpper.add(new OrderBy(identifier, orderBy.getDirection()));
    });

    // column 处理
    List<Column> upperColumns = buildColumnForUpperModel(midMetric, window);

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

  private List<Column> buildColumnForUpperModel(MetricBindQuery midMetric, Window window) {
    ColumnTableNameRewriter tableNameReplacer = new ColumnTableNameRewriter(
        midMetric.getName(),
        midMetric.getRelation().getTableName(),
        midMetric.getName()
    );

    // 指标维度列, 改写 tableName即可
    List<Column> upperColumns = new ArrayList<>();
    for (int i = 0; i < midMetric.getDimensions().size(); i++) {
      Column dimInMid = midMetric.getDimensions().get(i);
      Column column = (Column) tableNameReplacer.process(dimInMid);
      upperColumns.add(column);
    }

    // 指标列, 改tableName, 该字段名,  加window
    for (int i = 0; i < midMetric.getMetrics().size(); i++) {
      // 改 tableName
      Column metricInMid = midMetric.getMetrics().get(i);
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
          window
      );
      upperColumns.add(hasWindow);
    }

    return upperColumns;
  }

  /**
   * 构建 window, 返回的window已经处理过, 基于 fromRelation
   */
  private Window buildWindow(Identifier fromRelation) {
    YmlWindow ymlWindow = this.metricQuery.getWindow();

    // windowType
    WindowType windowType = WindowType.fromName(ymlWindow.getWindowType());
    if (windowType == null) {
      throw new ModelException("window type not support " + ymlWindow.getWindowType());
    }

    ColumnTableNameRewriter rewriter = new ColumnTableNameRewriter(fromRelation);
    // partitions
    List<Identifier> partitions = EMPTY_LIST;
    if (ymlWindow.getPartitions().size() > 0) {
      ImmutableList<String> ymlPartition = ymlWindow.getPartitions();
      partitions = new ArrayList<>(ymlPartition.size());
      for (int i = 0; i < ymlPartition.size(); i++) {
        String partitionCol = ymlPartition.get(i);
        Identifier col0 = (Identifier) ParserUtils.parseStandaloneExpression(partitionCol);
        Identifier col1 = (Identifier) rewriter.process(col0);
        partitions.add(col1);
      }
    }

    // orderBys
    List<OrderBy> orderBys = EMPTY_LIST;
    if (ymlWindow.getOrderBys().size() > 0) {
      ImmutableList<YmlOrderBy> ymlOrderBys = ymlWindow.getOrderBys();
      orderBys = new ArrayList<>(ymlOrderBys.size());
      for (int i = 0; i < ymlOrderBys.size(); i++) {
        YmlOrderBy ymlOrderBy = ymlOrderBys.get(i);
        Identifier col0 = (Identifier) ParserUtils.parseStandaloneExpression(ymlOrderBy.getName());
        Identifier col1 = (Identifier) rewriter.process(col0);
        OrderByDirection direction1 = OrderByDirection.fromName(ymlOrderBy.getDirection());
        orderBys.add(new OrderBy(col1, direction1));
      }
    }

    FrameType frameType = FrameType.fromName(ymlWindow.getFrameType());
    FrameBoundary start = parseFromYml(ymlWindow.getStart());
    FrameBoundary end = parseFromYml(ymlWindow.getEnd());

    return new Window(windowType, partitions, orderBys, frameType, start, end);
  }

  private FrameBoundary parseFromYml(YmlFrameBoundary boundary) {
    if (boundary == null) {
      throw new ModelException("window must have two boundary");
    }
    String ymlLeftBase = boundary.getBase();
    FrameBoundaryBase leftBase = FrameBoundaryBase.fromName(ymlLeftBase);
    if (leftBase == null) {
      throw new ModelException("window base not support " + ymlLeftBase);
    }
    return new FrameBoundary(leftBase, boundary.getOffset());
  }

}
