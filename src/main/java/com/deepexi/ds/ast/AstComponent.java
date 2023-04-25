package com.deepexi.ds.ast;

public abstract class AstComponent extends AstNode {

  public String getVersion() {
    return "v1";
  }

  public abstract ComponentType getComponentType();
}
