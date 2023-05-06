package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.UdfCastExpression;
import com.deepexi.ds.ast.expression.UdfExpression;
import com.deepexi.ds.ast.window.Window;
import java.util.List;
import java.util.Objects;

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

  @Override
  public AstNode visitUdfExpression(UdfExpression node, Void context) {
    UdfExpression udfExpression = (UdfExpression) super.visitUdfExpression(node, context);

    boolean isDataTypeCast = (node.getArgs().size() > 0)
        && Objects.equals(node.getName(), "cast");
    if (isDataTypeCast) {
      return new UdfCastExpression(udfExpression.getArgs());
    }
    return udfExpression;
  }

  @Override
  public AstNode visitColumn(Column node, Void context) {
    Window window = node.getWindow();
    Window newWindow = window;
    if (window != null) {
      newWindow = (Window) process(window, context);
    }
    Expression newExpr = (Expression) process(node.getExpr(), context);

    // 可能改写 dataType
    ColumnDataType dataType = node.getDataType();

    if (dataType == null && newExpr instanceof UdfCastExpression) {
      dataType = ((UdfCastExpression) newExpr).getToType();
    }

    return new Column(node.getAlias(), newExpr, dataType, newWindow);
  }
}