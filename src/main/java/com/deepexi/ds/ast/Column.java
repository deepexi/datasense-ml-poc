package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import lombok.Getter;

@Getter
public class Column extends AstNode {

  public static final Column ALL_COLUMN = new Column("*", Identifier.of("*"), null);
  private final Expression expr;
  private final String alias;
  private final ColumnDataType dataType;

  public Column(String alias, Expression expr, ColumnDataType dataType) {
    this.alias = alias;
    this.expr = expr;
    this.dataType = dataType;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitColumn(this, context);
  }

  public String toString() {
    return String.format("%s  =>  %s [%s ]", expr, alias, dataType);
  }

  public Column replaceExpr(Expression newExpr) {
    return new Column(alias, newExpr, dataType);
  }
}
