package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;

public abstract class BinaryExpression extends Expression {

  public abstract Expression getLeft();

  public abstract Expression getRight();

  public abstract BinaryOperator getOp();

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new RuntimeException("should not call this method");
  }

  public interface BinaryOperator {

    String getName();
  }

  public String toString() {
    Expression left = getLeft();
    String leftStr = left.toString();
    if (left instanceof BinaryExpression) {
      leftStr = "(" + leftStr + ")";
    }

    Expression right = getRight();
    String rightStr = right.toString();
    if (right instanceof BinaryExpression) {
      rightStr = "(" + rightStr + ")";
    }
    return leftStr + getOp().getName() + rightStr;
  }

}
