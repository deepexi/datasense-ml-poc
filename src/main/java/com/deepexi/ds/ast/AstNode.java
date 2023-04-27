package com.deepexi.ds.ast;

public abstract class AstNode {

  public abstract <R, C> R accept(AstNodeVisitor<R, C> visitor, C context);

}
