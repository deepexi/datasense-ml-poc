package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class MetricBindQuery extends Relation {

  private final Identifier name;                          // 该 metric 名
  private final Relation relation;                        // 该查询所依赖的 "表", from 子句
  private final ImmutableList<Column> metrics;            // 每个指标的, select中的聚合函数
  // 查询有关的条件
  private final ImmutableList<Expression> metricFilters;  // having 子句中的过滤条件
  private final ImmutableList<Column> dimensions;         // group by 子句中的列, 也会在 select中
  private final ImmutableList<Expression> modelFilters;   // where clause中的列

  // orderBy / limit /offset
  private final ImmutableList<OrderBy> orderBys;
  private final Integer limit;
  private final Integer offset;

  // 计算列
  private final ImmutableList<Column> columns;

  public MetricBindQuery(
      Identifier queryName,
      Relation relation,
      List<Expression> metricFilters,
      List<Column> dimensions,
      List<Expression> modelFilters,
      List<Column> metrics,
      List<OrderBy> orderBys,
      Integer limit,
      Integer offset) {
    this.name = queryName;
    this.relation = relation;
    this.metricFilters = ImmutableList.copyOf(metricFilters);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.metrics = ImmutableList.copyOf(metrics);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.limit = limit;
    this.offset = offset;

    // columns = dimension + metrics
    List<Column> columnsOfThisRelation = new ArrayList<>();
    columnsOfThisRelation.addAll(dimensions);
    columnsOfThisRelation.addAll(metrics);
    this.columns = ImmutableList.copyOf(columnsOfThisRelation);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitMetricBindQuery(this, context);
  }

  @Override
  public Identifier getTableName() {
    return getName();
  }

}
