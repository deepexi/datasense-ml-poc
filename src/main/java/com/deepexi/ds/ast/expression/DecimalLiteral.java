package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.AstNodeVisitor;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class DecimalLiteral extends Expression {

  private final BigDecimal value;

  public DecimalLiteral(BigDecimal value) {
    super();
    this.value = value;
  }

  public static DecimalLiteral of(BigDecimal v) {
    return new DecimalLiteral(v);
  }

  public static DecimalLiteral of(String v) {
    return new DecimalLiteral(new BigDecimal(v));
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new TODOException();
  }

  public String toString() {
    return "" + value;
  }
}