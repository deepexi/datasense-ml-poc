package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNodeVisitor;

public abstract class Literal extends Expression {

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new ModelException("should not be accessed");
  }
}
