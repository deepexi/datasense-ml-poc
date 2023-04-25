package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import lombok.Getter;

@Getter
public class Dimension extends AstNode {

  private final String name;
  private final Expression expr;
  private final String rawExpr;

  public Dimension(String name, Expression expr, String rawExpr) {
    this.name = name;
    this.expr = expr;
    this.rawExpr = rawExpr;
  }


  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitDimension(this, context);
  }

  public String toString() {
    return String.format("%s  =>  %s", expr, name);
  }

}
