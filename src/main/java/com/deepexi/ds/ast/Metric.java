package com.deepexi.ds.ast;


import com.deepexi.ds.ast.expression.Expression;
import java.util.List;
import lombok.Data;

@Data
public class Metric extends AstComponent {

  private final String name;
  private final Model model;
  private final List<Dimension> dimensions;
  private final Expression aggregate;
  private final Column output;
  private final Expression where;

  @Override
  public ComponentType getComponentType() {
    return ComponentType.METRICS_ML;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return null;
  }
}
