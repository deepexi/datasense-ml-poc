package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.List;

/**
 * 把函数中的 Identifier进行处理
 * <pre>
 * tableX.colA =>
 * tableY.colA
 * </pre>
 */
public class ColumnInFunctionHandler extends BaseColumnIdentifierRewriter {

  private final List<Relation> scopes;

  public ColumnInFunctionHandler(List<Relation> scopes) {
    this.scopes = scopes;
  }

  @Override
  public AstNode visitIdentifier(Identifier node, Void context) {
    if (node.getPrefix() == null) {
      return node;
    }
    String table = node.getPrefix();
    String field = node.getValue();

    // check table.field in relation
    Relation targetRelation = null;
    for (int i = 0; i < scopes.size(); i++) {
      Relation toSearch = scopes.get(i);
      Column column = toSearch.getColumn(field);
      if (column != null && column != Column.ALL_COLUMN) {
        if (targetRelation == null) {
          targetRelation = toSearch;
        } else {
          throw new ModelException("multiple relation has same column");
        }
      }
    }

    if (targetRelation == null) {
      throw new ModelException(String.format("column %s.%s cannot recognized", table, field));
    }
    return new Identifier(targetRelation.getTableName().getValue(), field);
  }
}