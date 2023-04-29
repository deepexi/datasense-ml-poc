package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.expression.BinaryExpression.BinaryOperator;
import java.util.Arrays;
import java.util.Objects;

public enum CompareOperator implements BinaryOperator {
  EQUAL("="),
  GT(">"),
  GTE(">="),
  LT("<"),
  LTE("<="),
  NOT_EQUAL("<>"),
  ;
  public final String name;

  CompareOperator(String name) {
    this.name = name;
  }

  public static CompareOperator fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(CompareOperator.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }

  @Override
  public String getName() {
    return name;
  }
}