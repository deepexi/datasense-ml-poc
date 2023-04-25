package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Identifier;
import lombok.Getter;

@Getter
public class Column extends AstNode {

  public static final Column ALL_COLUMN = new Column("*", Identifier.of("*"), null, "*");
  private final Identifier expr;
  private final String alias;
  private final ColumnDataType type;
  private final String rawExpr;

  public Column(String alias, Identifier expr, ColumnDataType type, String rawExpr) {
    this.alias = alias;
    this.expr = expr;
    this.type = type;
    this.rawExpr = rawExpr;
  }


  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitColumn(this, context);
  }

  public String toString() {
    return String.format("%s  =>  %s [%s ]", expr, alias, type);
  }
}
