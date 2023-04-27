package com.deepexi.ds.ast.expression.condition;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.expression.Expression;
import lombok.Getter;

@Getter
public class BinaryExpression extends Expression {

  private final Expression left;
  private final Expression right;
  private final BinaryOperator op;

  public BinaryExpression(Expression left, Expression right, BinaryOperator op) {
    this.left = left;
    this.right = right;
    this.op = op;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitCompareExpression(this, context);
  }

  @Override
  public String toString() {
    return left.toString() + op.name + right.toString();
  }

  public enum BinaryOperator {
    EQUAL("="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    NOT_EQUAL("<>"),
    ;
    public final String name;

    BinaryOperator(String name) {
      this.name = name;
    }
  }
}
