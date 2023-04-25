package com.deepexi.ds.ast.source;

import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ModelVisitor;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;
import lombok.Getter;

/**
 * this source is from an external table
 */
@Getter
public class TableSource extends Source {

  private final String dataSource;
  private final Identifier tableName;

  public TableSource(String dataSource, Identifier tableName) {
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitTableSource(this, context);
  }

  @Override
  public String toString() {
    return tableName + ", dataSource=" + dataSource;
  }

  @Override
  public Identifier getAlias() {
    return tableName;
  }

  @Override
  public List<Column> getColumns() {
    return null;
  }
}