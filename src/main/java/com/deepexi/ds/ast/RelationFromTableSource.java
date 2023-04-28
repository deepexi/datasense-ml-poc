package com.deepexi.ds.ast;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ast.visitor.analyzer.ScopeCollectorContext;
import java.util.List;
import lombok.Getter;

@SuppressWarnings("unchecked")
@Getter
public class RelationFromTableSource extends Relation {

  private final TableSource tableSource;
  private final ScopeCollectorContext context;

  public RelationFromTableSource(TableSource tableSource, ScopeCollectorContext context) {
    this.tableSource = tableSource;
    this.context = context;
  }

  @Override
  public Identifier getTableName() {
    return tableSource.getTableName();
  }

  @Override
  public List<Column> getColumns() {
    return EMPTY_LIST;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitRelationFromTableSource(this, context);
  }
}