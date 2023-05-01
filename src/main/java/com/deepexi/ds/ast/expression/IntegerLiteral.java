package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import lombok.Getter;

@Getter
public class IntegerLiteral extends Expression {

  private final Integer value;

  public IntegerLiteral(Integer value) {
    super();
    this.value = value;
  }

  public static IntegerLiteral of(int v) {
    return new IntegerLiteral(v);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitIntegerLiteral(this, context);
  }

  public String toString() {
    return String.valueOf(value);
  }
}