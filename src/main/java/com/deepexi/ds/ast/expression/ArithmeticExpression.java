package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ArithmeticExpression extends BinaryExpression {

  private final Expression left;
  private final ArithmeticOperator op;
  private final Expression right;

  public ArithmeticExpression(Expression left, Expression right, ArithmeticOperator op) {
    this.left = left;
    this.op = op;
    this.right = right;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitArithmeticExpression(this, context);
  }

  public enum ArithmeticOperator implements BinaryOperator {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    ;
    public final String name;

    ArithmeticOperator(String name) {
      this.name = name;
    }

    public static ArithmeticOperator fromName(String name) {
      if (name == null) {
        return null;
      }
      String lowerName = name.toLowerCase();
      return Arrays.stream(ArithmeticOperator.values())
          .filter(x -> Objects.equals(x.name, lowerName))
          .findAny().orElse(null);
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
