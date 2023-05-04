package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.window.Window;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Column extends AstNode {

  public static final Column ALL_COLUMN = new Column("*", Identifier.of("*"), null);
  private final String alias;
  private final Expression expr;          // maybe null
  private final ColumnDataType dataType;  // maybe null
  private final Window window;            // maybe null

  public Column(@NonNull String alias, @NonNull Expression expr, ColumnDataType dataType) {
    this.alias = alias;
    this.expr = expr;
    this.dataType = dataType;
    this.window = null;
  }

  public Column(String alias, Expression expr, ColumnDataType dataType, Window window) {
    this.alias = alias;
    this.expr = expr;
    this.dataType = dataType;
    this.window = window;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitColumn(this, context);
  }

  public String toString() {
    return String.format("%s  AS  %s", expr, alias);
  }

}
