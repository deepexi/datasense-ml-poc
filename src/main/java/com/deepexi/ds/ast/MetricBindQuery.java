package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class MetricBindQuery extends Relation {

  private final Identifier name;
  private final Model model;
  private final ImmutableList<Expression> metricFilters;
  private final ImmutableList<Column> dimensions;
  private final ImmutableList<Expression> modelFilters;
  private final ImmutableList<Column> metrics;

  public MetricBindQuery(Identifier queryName, Model model,
      List<Expression> metricFilters,
      List<Column> dimensions,
      List<Expression> modelFilters,
      List<Column> metrics
  ) {
    this.name = queryName;
    this.model = model;
    this.metricFilters = ImmutableList.copyOf(metricFilters);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.metrics = ImmutableList.copyOf(metrics);
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
