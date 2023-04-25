package com.deepexi.ds.ast;

public abstract class AstNode {

//  public abstract AstNode getParent();

  public abstract <R, C> R accept(ModelVisitor<R, C> visitor, C context);

}
