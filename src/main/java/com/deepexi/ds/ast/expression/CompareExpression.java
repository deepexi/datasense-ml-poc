package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import lombok.Getter;

@Getter
public class CompareExpression extends BinaryExpression {

  private final Expression left;
  private final Expression right;
  private final CompareOperator op;

  public CompareExpression(Expression left, Expression right, CompareOperator op) {
    this.left = left;
    this.right = right;
    this.op = op;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitCompareExpression(this, context);
  }
}
