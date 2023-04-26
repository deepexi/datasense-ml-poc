package com.deepexi.ds.builder;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Dimension;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.builder.express.MetricExpressionParser;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetricBindQueryBuilder {

  private YmlMetricQuery metricQuery;

  // 需要查询的指标
  private Set<YmlMetric> metrics;

  // 关联的 model, 如果一个孤立的model 不被引用, 将不会出现在此集合中
  private Set<Model> models;

  public MetricBindQueryBuilder(YmlFullQuery ymlFullQuery) {
    checkIntegrity(ymlFullQuery);
  }

  public MetricBindQuery build() {
    MetricBindQuery query = metricsOnSameModel();
    if (query == null) {
      throw new TODOException("目前仅支持针对同一个model的多指标查询");
    }
    return query;
  }

  private void checkIntegrity(YmlFullQuery ymlFullQuery) {
    this.metricQuery = ymlFullQuery.getQuery();
    if (metricQuery == null) {
      throw new ModelException("metric query must not be null");
    }

    //
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

    models = new HashSet<>();
    Map<String, YmlModel> existModels = ymlFullQuery.getModels().stream()
        .collect(Collectors.toMap(YmlModel::getName, Function.identity()));
    Set<String> minDims = new HashSet<>(this.metricQuery.getDimensions());
    for (YmlMetric ele : this.metrics) {
      // 所有 metrics.dimension 必定包含 query.dimension
      boolean metricHasAllDims = ele.getDimensions().containsAll(minDims);
      if (!metricHasAllDims) {
        throw new ModelException(String.format("query[%s] 包含的维度 超过 metric=[%s]的预设范围",
            this.metricQuery.getName(), ele.getName()));
      }
      ModelBuilder b = new ModelBuilder(ymlFullQuery.getModels(),
          existModels.get(ele.getModelName()));
      models.add(b.build());
    }
    if (metrics.size() == 0) {
      throw new ModelException("found no model_def");
    }
  }

  /**
   * 针对一个 model 的多指标查询
   */
  private MetricBindQuery metricsOnSameModel() {
    if (models.size() > 1) {
      return null;
    }

    // root model
    Model model = this.models.stream().findAny().get();

    // all metrics must against SAME root model
    boolean allSameModel = this.metrics.stream()
        .allMatch(m -> Objects.equals(m.getModelName(), model.getName().getValue()));
    if (!allSameModel) {
      return null;
    }

    // collect all metrics
    List<Column> columns = new ArrayList<>();
    for (YmlMetric m : this.metrics) {
      String alias = m.getName();
      String rawExpr = m.getAggregate();
      ColumnDataType dataType = ColumnDataType.fromName(m.getDataType());
      // TODO: 解析聚合函数
      StringLiteral expr = StringLiteral.of(rawExpr);
      Column column = new Column(alias, expr, dataType, rawExpr);
      columns.add(column);
    }
    // name
    Identifier metricQueryName = Identifier.of(metricQuery.getName());

    // dimension的处理策略: 取 YmlMetricQuery.query对象中的 dimension
    final List<Dimension> dimensions = new ArrayList<>();
    metricQuery.getDimensions().forEach(d -> {
      // 解析维度
      Expression expr = StringLiteral.of(d);
      Dimension dim = new Dimension(d, expr, d);
      dimensions.add(dim);
    });

    // metricFilters
    final List<Expression> metricFilters = new ArrayList<>();
    metricQuery.getMetricFilters().forEach(metricFilter -> {
      MetricExpressionParser parser = new MetricExpressionParser(metrics, metricFilter);
      Expression expr = parser.parse();
      metricFilters.add(expr);
    });

    // modelFilters
    final List<Expression> modelFilters = new ArrayList<>();
    metricQuery.getModelFilters().forEach(f -> {
      Expression expr = StringLiteral.of(f);
      modelFilters.add(expr);
    });

    return new MetricBindQuery(
        metricQueryName,
        model,
        metricFilters,
        dimensions,
        modelFilters,
        columns);
  }
}
