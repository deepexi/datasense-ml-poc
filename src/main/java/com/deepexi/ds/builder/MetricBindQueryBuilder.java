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
import com.deepexi.ds.ast.Window;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.builder.express.AddTableNameToColumnRewriter;
import com.deepexi.ds.builder.express.AddTableNameToColumnRewriter.AvailTableContext;
import com.deepexi.ds.builder.express.BoolConditionParser;
import com.deepexi.ds.builder.express.MetricExpressionParser;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlMetricQuery.YmlOrderBy;
import com.deepexi.ds.ymlmodel.YmlModel;
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
    for (YmlMetric m : this.metrics) {
      String alias = m.getName();
      Expression expression = ParserUtils.parseStandaloneExpression(m.getAggregate());
      ColumnDataType dataType = ColumnDataType.fromName(m.getDataType());
      Column rawCol = new Column(alias, expression, dataType);
      AvailTableContext context = new AvailTableContext(model4Metrics.getName());
      Column column = (Column) new AddTableNameToColumnRewriter().process(rawCol, context);
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
      String direction = ymlOrderBy.getDirection();
      OrderByDirection direction1 = OrderByDirection.fromName(direction);
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

    if (true) {
      throw new RuntimeException("in progress");
    }
    // 包含 window, 需要创建一个额外的 MetricBindQuery, 记做 midMetric
    // 在 midMetric完成 group by
    // 之上还有一层 MetricBindQuery, 完成窗口运算
    String midMetricName = metricQuery.getName() + "__mid";
    MetricBindQuery midMetric = new MetricBindQuery(Identifier.of(midMetricName),
        model4Metrics, metricFilters, dimensions,
        modelFilters, columns, EMPTY_LIST, null, null);

    // window
    Window window = buildWindow();

    return new Model(
        metricQueryName,
        midMetric,
        EMPTY_LIST,
        columns,
        EMPTY_LIST,
        orderBys,
        metricQuery.getLimit(),
        metricQuery.getOffset()
    );
  }

  private Window buildWindow() {
    return null;
  }

}
