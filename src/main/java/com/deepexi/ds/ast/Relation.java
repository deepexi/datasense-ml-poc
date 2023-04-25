package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;

/**
 * something looks like a table
 */
public abstract class Relation extends AstNode {

  public abstract Identifier getTableName();

  public abstract List<Column> getColumns();

}
