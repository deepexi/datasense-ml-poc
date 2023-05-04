package com.deepexi.ds.builder.express;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.window.FrameBoundary;
import java.util.Objects;
import lombok.NonNull;

/**
 * <pre>
 * tableA.colX =>
 * tableA.colY
 * </pre>
 */
public class ColumnNameRewriter extends BaseColumnIdentifierRewriter {

  private final Identifier fromRelation;
  private final String toValue;

  public ColumnNameRewriter(
      @NonNull Identifier fromRelation,
      @NonNull String toValue) {
    this.fromRelation = fromRelation;
    this.toValue = toValue;
  }


  @Override
  public AstNode visitIdentifier(Identifier node, Void context) {
    if (node.getPrefix() == null) {
      return node;
    }
    Identifier srcTable = Identifier.of(node.getPrefix());
    if (!Objects.equals(srcTable, fromRelation)) {
      return node;
    }
    return new Identifier(node.getPrefix(), toValue);
  }

}
