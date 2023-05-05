package com.deepexi.ds.ast;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@SuppressWarnings("unchecked")
@Getter
public class Model extends Relation {

  protected final Identifier name;
  protected final Relation source;
  protected final ImmutableList<Join> joins;
  protected final ImmutableList<Column> columns;
  protected final ImmutableList<Column> dimensions;

  // orderBy / limit /offset
  private final ImmutableList<OrderBy> orderBys;
  private final Integer limit;
  private final Integer offset;

  public Model(Identifier name, Relation source, List<Join> joins, List<Column> columns,
      List<Column> dimensions) {
    this.name = name;
    this.columns = ImmutableList.copyOf(columns);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.joins = ImmutableList.copyOf(joins);
    this.source = source;
    // optional
    this.orderBys = ImmutableList.copyOf(EMPTY_LIST);
    this.limit = null;
    this.offset = null;
  }

  public Model(Identifier name,
      Relation source,
      List<Join> joins,
      List<Column> columns,
      List<Column> dimensions,
      List<OrderBy> orderBys,
      Integer limit,
      Integer offset) {
    this.name = name;
    this.columns = ImmutableList.copyOf(columns);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.joins = ImmutableList.copyOf(joins);
    this.source = source;
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.limit = limit;
    this.offset = offset;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitModel(this, context);
  }

  @Override
  public String toString() {
    return "model: " + name.toString();
  }

  @Override
  public Identifier getTableName() {
    return name;
  }

  @Override
  public Relation getFrom() {
    return source;
  }

  @Override
  public List<Join> getJoin() {
    return joins;
  }

  @Override
  public boolean hasAnyColumn() {
    return false;
  }
}
