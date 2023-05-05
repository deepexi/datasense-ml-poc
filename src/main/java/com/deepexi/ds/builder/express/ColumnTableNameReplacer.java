package com.deepexi.ds.builder.express;

import com.deepexi.ds.DevConfig;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.Objects;

/**
 * 把字段的表名进行替换
 * <pre>
 * tableX.colA =>
 * tableY.colA
 * </pre>
 */
public class ColumnTableNameReplacer extends BaseColumnIdentifierRewriter {

  private final boolean debug = false;
  private final Identifier fromTable;
  private final Identifier toTable;

  public ColumnTableNameReplacer(Identifier fromTable, Identifier toTable) {
    this.fromTable = fromTable;
    this.toTable = toTable;
  }

  @Override
  public AstNode visitIdentifier(Identifier node, Void context) {
    if (node.getPrefix() != null) {
      Identifier srcTable = Identifier.of(node.getPrefix());
      if (Objects.equals(srcTable, fromTable)) {
        if (DevConfig.DEBUG) {
          System.out.println("Column replace tableName: "
              + node.getPrefix() + "." + node.getValue()
              + " => "
              + toTable.getValue() + "." + node.getValue());
        }
        return new Identifier(toTable.getValue(), node.getValue());
      }
    }
    // other case, don't replace
    return node;
  }
}