package com.deepexi.ds.parser;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.CompareOperator;

public class CompareExpressionBuilder extends Expression {

  private Expression left;
  private Expression right;
  private CompareOperator op;

  public CompareExpressionBuilder left(Expression left) {
    this.left = left;
    return this;
  }

  public CompareExpressionBuilder right(Expression right) {
    this.right = right;
    return this;
  }

  public CompareExpressionBuilder op(CompareOperator op) {
    this.op = op;
    return this;
  }

  public CompareExpression build() {
    return new CompareExpression(left, right, op);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new ModelException("shouldn't be called");
  }
}
