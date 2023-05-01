package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class MetricBindQuery extends Relation {

  private final Identifier name;                  // 该 metric 名
  // private final Model model;                      // 目前可以是 Model 和 Metric
  private final Relation relation;                      // 目前可以是 Model 和 Metric
  private final ImmutableList<Column> metrics;    // 每个指标的
  // 查询有关的条件
  private final ImmutableList<Expression> metricFilters;
  private final ImmutableList<Column> dimensions;
  private final ImmutableList<Expression> modelFilters;

  // orderBy / limit /offset
  private final ImmutableList<OrderBy> orderBys;
  private final Integer limit;
  private final Integer offset;

  public MetricBindQuery(
      Identifier queryName,
      // Model model,
      Relation relation,
      List<Expression> metricFilters,
      List<Column> dimensions,
      List<Expression> modelFilters,
      List<Column> metrics,
      List<OrderBy> orderBys,
      Integer limit,
      Integer offset) {
    this.name = queryName;
    // this.model = model;
    this.relation = relation;
    this.metricFilters = ImmutableList.copyOf(metricFilters);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.metrics = ImmutableList.copyOf(metrics);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.limit = limit;
    this.offset = offset;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitMetricBindQuery(this, context);
  }

  @Override
  public Identifier getTableName() {
    return getName();
  }

  @Override
  public List<Column> getColumns() {
    return null;
  }
}
