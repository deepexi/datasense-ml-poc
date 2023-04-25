package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.ModelVisitor;
import lombok.Getter;

@Getter
public class StringLiteral extends Expression {

  private final String value;

  public StringLiteral(String s) {
    super();
    this.value = s;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitStringLiteral(this, context);
  }

  public String toString() {
    return value;
  }

  public static StringLiteral of(String v) {
    return new StringLiteral(v);
  }
}