package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.ColumnDataType;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class UdfCastExpression extends UdfExpression {

  public static final String NAME = "cast";
  private final Identifier castColId;
  private final ColumnDataType fromType;
  private final ColumnDataType toType;
  private final List<Expression> castArgs;

  public UdfCastExpression(List<Expression> args) {
    super(NAME, args);

    castColId = (Identifier) args.get(0);
    DataTypeLiteral from = (DataTypeLiteral) args.get(1);
    fromType = ColumnDataType.fromName(from.getValue());

    DataTypeLiteral to = (DataTypeLiteral) args.get(2);
    toType = ColumnDataType.fromName(to.getValue());

    List<Expression> tmp = new ArrayList<>(args.size() - 3);
    for (int i = 3; i < args.size(); i++) {
      tmp.add(args.get(i));
    }
    this.castArgs = ImmutableList.copyOf(tmp);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new RuntimeException("TODO");
  }

  @Override
  public String toString() {
    String argsJoin = castArgs.stream().map(Object::toString).collect(Collectors.joining(","));
    return String.format("%s: %s => %s args=(%s)", castColId.toString(),
        fromType.name,
        toType.name,
        argsJoin);
  }
}
