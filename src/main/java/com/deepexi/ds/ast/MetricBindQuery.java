package com.deepexi.ds.ast;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class MetricBindQuery extends AstComponent {

  private final Identifier name;
  private final Model model;
  private final ImmutableList<Expression> dimFilters;
  private final ImmutableList<Dimension> dimensions;
  private final ImmutableList<Expression> modelFilters;
  private final ImmutableList<Column> metrics;

  public MetricBindQuery(Identifier queryName, Model model,
      List<Expression> dimFilters,
      List<Dimension> dimensions,
      List<Expression> modelFilters,
      List<Column> metrics
  ) {
    this.name = queryName;
    this.model = model;
    this.dimFilters = ImmutableList.copyOf(dimFilters);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.metrics = ImmutableList.copyOf(metrics);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentType.METRICS_DEF;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitMetricBindQuery(this, context);
  }
}
