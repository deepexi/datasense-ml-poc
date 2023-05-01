package com.deepexi.ds.ast.source;


import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;
import lombok.Getter;

/**
 * this source from another Model
 */
@Getter
public class ModelSource extends Relation {

  private final Model model;

  public ModelSource(Model model) {
    this.model = model;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitModelSource(this, context);
  }

  @Override
  public String toString() {
    return model.getName().getValue();
  }

  @Override
  public Identifier getTableName() {
    return model.getName();
  }

  @Override
  public List<Column> getColumns() {
    return model.getColumns();
  }
}
