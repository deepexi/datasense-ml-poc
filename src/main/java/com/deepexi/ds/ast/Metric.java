package com.deepexi.ds.ast;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

/**
 * metric 对应指标的计算逻辑.
 */
@Getter
@SuppressWarnings("unchecked")
public class Metric extends Relation {

  private final Identifier name;
  private final Relation source;                          // 该查询所依赖的 "表", from 子句
  private final ImmutableList<Column> groupBy;            // group by 子句中的列, 也会在 select中
  private final ImmutableList<Column> columns;            // 列

  public Metric(
      Identifier name,
      Relation source,
      ImmutableList<Column> groupBy,
      ImmutableList<Column> columns) {
    this.name = name;
    this.source = source;
    this.groupBy = groupBy;
    this.columns = columns;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new TODOException();
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
  public List<Join> getJoins() {
    return EMPTY_LIST;
  }

  @Override
  public boolean hasAnyColumn() {
    return false;
  }
}
