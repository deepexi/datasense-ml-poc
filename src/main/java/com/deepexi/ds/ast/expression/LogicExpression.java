package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.expression.Expression;
import lombok.Data;

@Data
public class LogicExpression {

  private final Expression left;
  private final Expression right;
  private final LogicOperator op;

  public LogicExpression(Expression left, Expression right, LogicOperator op) {
    this.left = left;
    this.right = right;
    this.op = op;
  }

  public enum LogicOperator {
    AND("and"),
    OR("or"),
    NOT("not"),
    ;
    public final String name;

    LogicOperator(String name) {
      this.name = name;
    }
  }
}
