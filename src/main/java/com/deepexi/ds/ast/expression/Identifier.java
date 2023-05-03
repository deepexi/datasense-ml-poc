package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class Identifier extends Expression {

  public final static String IDENTIFIER_SEPARATOR = ".";
  public final static String RE_IDENTIFIER_SEPARATOR = "\\.";
  private final String prefix;
  private final String value;

  public Identifier(String prefix, String value) {
    this.prefix = prefix;
    this.value = value;
  }

  public static Identifier of(String value) {
    java.util.Objects.requireNonNull(value);
    return new Identifier(null, value);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitIdentifier(this, context);
  }

  @Override
  public String toString() {
    if (prefix != null) {
      return prefix + IDENTIFIER_SEPARATOR + value;
    } else {
      return value;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Identifier)) {
      return false;
    }
    Identifier that = (Identifier) o;
    return Objects.equal(prefix, that.prefix)
        && Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(prefix, value);
  }

  public Expression replacePrefix(String prefix) {
    return new Identifier(prefix, value);
  }
}
