package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;

/**
 * something looks like a table
 */
public abstract class Relation extends AstNode {

  public abstract Identifier getTableName();

  public abstract List<Column> getColumns();

  public abstract Relation getFrom();

  public abstract List<Join> getJoins();

  /**
   * 是否包含任意列, 用于某些 "select * " 构建的 Relation
   *
   * @return true: 包含任意列, false: 包含特定列
   */
  public abstract boolean hasAnyColumn();

  public Column getColumn(String colName) {
    if (hasAnyColumn()) {
      return Column.ALL_COLUMN;
    }

    List<Column> columns = getColumns();
    if (columns == null || columns.size() == 0) {
      return null;
    }
    return columns.stream()
        .filter(column -> column.getAlias().equals(colName))
        .findAny().orElse(null);
  }
}
