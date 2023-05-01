package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class Model extends Relation {

  protected final Identifier name;
  //  protected final Source source;
  protected final Relation source;
  protected final ImmutableList<Join> joins;
  protected final ImmutableList<Column> columns;
  protected final ImmutableList<Column> dimensions;

  public Model(Identifier name, Relation source, List<Join> joins, List<Column> columns,
      List<Column> dimensions) {
    this.name = name;
    this.columns = ImmutableList.copyOf(columns);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.joins = ImmutableList.copyOf(joins);
    this.source = source;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitModel(this, context);
  }

  @Override
  public String toString() {
    return name.toString();
  }

  @Override
  public Identifier getTableName() {
    return name;
  }
}
