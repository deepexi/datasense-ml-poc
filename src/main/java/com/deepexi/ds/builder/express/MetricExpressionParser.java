package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * <pre>
 * 指标表达式处理
 * 定义指标  metric_a: sum(profit)
 * 查询指标  metrics: [metric_a]
 * 过滤指标  metric_filters: [ metric_a > 100 ], 需要提取其表达式 ==> sum(profit) > 100
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
    // 第一步 简单解析
    // 第二步 把指标name 替换为 指标表达式
    Expression expr1 = ParserUtils.parseBooleanExpression(dimFilter);

    if (expr1 instanceof CompareExpression) {
      CompareExpression comp = (CompareExpression) expr1;

      Expression left = comp.getLeft();
      Expression newLeft = left;
      if (left instanceof Identifier && ((Identifier) left).getPrefix() == null) {
        Identifier identifier = (Identifier) left;
        newLeft = getMetricExpressionByName(identifier.getValue());
      }

      Expression right = comp.getRight();
      Expression newright = right;
      if (right instanceof Identifier && ((Identifier) right).getPrefix() == null) {
        Identifier identifier = (Identifier) right;
        newright = getMetricExpressionByName(identifier.getValue());
      }

      return new CompareExpression(newLeft, newright, comp.getOp());
    }
    throw new TODOException("TODO");
  }

  private Expression getMetricExpressionByName(String name) {
    YmlMetric metric = metricDef.stream()
        .filter(metricDef -> metricDef.getName().equals(name))
        .findAny().orElse(null);
    if (metric == null) {
      return new StringLiteral(name);
    }
    return new StringLiteral(metric.getAggregate());
  }
}
