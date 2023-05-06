package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class DataTypeLiteral extends Literal {

  private final String value;

  public DataTypeLiteral(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DataTypeLiteral)) {
      return false;
    }
    DataTypeLiteral that = (DataTypeLiteral) o;
    return Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitDataTypeLiteral(this, context);
  }
}
