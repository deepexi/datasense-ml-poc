package com.deepexi.ds.ast.source;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;
import lombok.Getter;

/**
 * this source is from an external table
 */
@SuppressWarnings("unchecked")
@Getter
public class TableSource extends Relation {

  private final String dataSource;
  private final Identifier tableName;

  public TableSource(String dataSource, Identifier tableName) {
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitTableSource(this, context);
  }

  @Override
  public String toString() {
    return "table: " + tableName + ", dataSource=" + dataSource;
  }

  @Override
  public List<Column> getColumns() {
    return null;
  }

  @Override
  public Relation getFrom() {
    return null;
  }

  @Override
  public List<Join> getJoins() {
    return EMPTY_LIST;
  }

  @Override
  public boolean hasAnyColumn() {
    return true;
  }
}