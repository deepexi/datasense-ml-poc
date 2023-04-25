package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.ModelVisitor;
import lombok.Getter;

@Getter
public class IntegerLiteral extends Expression {

  private final Integer value;

  public IntegerLiteral(Integer value) {
    super();
    this.value = value;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitIntegerLiteral(this, context);
  }

  public String toString() {
    return "" + value;
  }
}