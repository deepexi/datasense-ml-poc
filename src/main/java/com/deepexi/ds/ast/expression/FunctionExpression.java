package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class FunctionExpression extends Expression {

  private final String name;
  private final ImmutableList<Expression> args;

  public FunctionExpression(String name, List<Expression> args) {
    this.name = name;
    this.args = ImmutableList.copyOf(args);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new RuntimeException("TODO");
  }

  @Override
  public String toString() {
    String argsJoin = args.stream().map(Object::toString).collect(Collectors.joining(","));
    return String.format("%s(%s)", name, argsJoin);
  }
}