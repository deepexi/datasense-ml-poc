package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.CompareOperator;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * <pre>
 * 指标表达式处理
 * 定义指标时  metric_a, ==> sum(profit)
 * 查询指标时, metrics: [metric_a]
 * 过滤指标时, dimFilter: [ metric_a > 100 ]
 * 需要把这些关联起来
 * </pre>
 */
public class MetricExpressionParser {

  private final ImmutableSet<YmlMetric> metricDef;
  private final String dimFilter;

  public MetricExpressionParser(Set<YmlMetric> metricDef, String dimFilter) {
    this.metricDef = ImmutableSet.copyOf(metricDef);
    this.dimFilter = dimFilter;
  }

  public Expression parse() {
    // String dimFilter = "metric_sum_ss_net_paid__store_sales > 1000000";
    CompareOperator op = ExpressionParseUtils.extractBinaryOperator(dimFilter);
    if (op == null) {
      throw new TODOException(String.format("目前仅支持 比较表达式, 当前表达式=%s", dimFilter));
    }

    String[] parts = dimFilter.split(op.name);
    if (parts.length != 2) {
      throw new TODOException(String.format("目前仅支持 比较表达式, 当前表达式=%s", dimFilter));
    }

    Expression left = parseOperator(parts[0].trim());
    Expression right = parseOperator(parts[1].trim());
    return new CompareExpression(left, right, op);
  }

  private Expression parseOperator(String name) {
    YmlMetric metric = metricDef.stream()
        .filter(metricDef -> metricDef.getName().equals(name))
        .findAny().orElse(null);
    if (metric == null) {
      return new StringLiteral(name);
    }
    return new StringLiteral(metric.getAggregate());
  }

}
