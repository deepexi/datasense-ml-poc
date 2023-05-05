package com.deepexi.ds.builder.express;

import com.deepexi.ds.DevConfig;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;

/**
 * 把没有表名的字段, 添加上默认的表名 该类 应用于 Column AstNode, 不可乱用
 * <pre>
 * colA =>
 * tableX.colA
 * </pre>
 */
public class ColumnTableNameAdder extends BaseColumnIdentifierRewriter {

  private final String tableName;

  public ColumnTableNameAdder(Relation relation) {
    tableName = relation.getTableName().getValue();
  }

  @Override
  public AstNode visitIdentifier(Identifier node, Void context) {
    if (node.getPrefix() != null) {
      return node;
    }
    if (DevConfig.DEBUG) {
      System.out.println("Column add tableName: "
          + node.getValue()
          + " => "
          + tableName + "." + node.getValue());
    }
    return new Identifier(tableName, node.getValue());
  }
}
