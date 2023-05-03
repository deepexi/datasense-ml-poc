package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import lombok.Getter;

@Getter
public class BooleanLiteral extends Expression {

  private final boolean value;

  public BooleanLiteral(boolean value) {
    super();
    this.value = value;
  }

  public static BooleanLiteral of(boolean v) {
    return new BooleanLiteral(v);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitBooleanLiteral(this, context);
  }

  public String toString() {
    return String.valueOf(value);
  }

  public static BooleanLiteral TRUE = BooleanLiteral.of(true);
  public static BooleanLiteral FALSE = BooleanLiteral.of(false);
}