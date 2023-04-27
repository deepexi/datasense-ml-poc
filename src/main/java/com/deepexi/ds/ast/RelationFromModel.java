package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.visitor.analyzer.ScopeCollectorContext;
import java.util.List;
import lombok.Getter;

@Getter
public class RelationFromModel extends Relation {

  private final Model model;
  private final ScopeCollectorContext context;

  public RelationFromModel(Model model, ScopeCollectorContext context) {
    this.model = model;
    this.context = context;
  }

  @Override
  public Identifier getTableName() {
    return model.getName();
  }

  @Override
  public List<Column> getColumns() {
    return model.getColumns();
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitRelationFromModel(this, context);
  }
}