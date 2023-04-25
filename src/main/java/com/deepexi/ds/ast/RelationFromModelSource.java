package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.visitor.analyzer.ScopeCollectorContext;
import java.util.List;
import lombok.Getter;

@Getter
public
class RelationFromModelSource extends Relation {

  private final ModelSource modelSource;
  private final ScopeCollectorContext context;

  public RelationFromModelSource(ModelSource modelSource, ScopeCollectorContext context) {
    this.modelSource = modelSource;
    this.context = context;
  }

  @Override
  public Identifier getTableName() {
    return modelSource.getTableName();
  }

  @Override
  public List<Column> getColumns() {
    Model model = modelSource.getModel();
    return model.getColumns();
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitRelationFromModelSource(this, context);
  }
}