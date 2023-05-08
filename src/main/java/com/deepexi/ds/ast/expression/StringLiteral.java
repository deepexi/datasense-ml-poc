package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class StringLiteral extends Literal {

  private static final String singleQuote = "'";
  private final String value;

  public StringLiteral(String s) {
    super();
    this.value = s;
  }

  public String removeSingleQuote() {
    if (value.startsWith(singleQuote) && value.endsWith(singleQuote)) {
      return value.substring(1, value.length() - 2);
    }
    return value;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitStringLiteral(this, context);
  }

  public String toString() {
    return value;
  }

  public static StringLiteral of(String v) {
    return new StringLiteral(v);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StringLiteral)) {
      return false;
    }
    StringLiteral that = (StringLiteral) o;
    return Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}