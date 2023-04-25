package com.deepexi.ds.ast.source;

import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.Relation;

public abstract class Source extends Relation {

  public abstract Identifier getAlias();
}
