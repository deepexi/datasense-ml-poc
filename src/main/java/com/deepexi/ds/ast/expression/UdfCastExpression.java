package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.ColumnDataType;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * 类型转换
 */
@Getter
public class UdfCastExpression extends UdfExpression {

  public static final String NAME = "cast";
  private final Identifier castColId;
  private final ColumnDataType toType;
  private final List<Expression> castArgs;

  public UdfCastExpression(List<Expression> args) {
    super(NAME, args);

    castColId = (Identifier) args.get(0);

    DataTypeLiteral from = (DataTypeLiteral) args.get(1);
    toType = ColumnDataType.fromName(from.getValue());

    int startArgIndex = 2;
    List<Expression> tmp = new ArrayList<>(args.size() - startArgIndex);
    for (int i = startArgIndex; i < args.size(); i++) {
      tmp.add(args.get(i));
    }
    this.castArgs = ImmutableList.copyOf(tmp);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitUdfCastExpression(this, context);
  }

  @Override
  public String toString() {
    String argsJoin = castArgs.stream().map(Object::toString).collect(Collectors.joining(","));
    return String.format("%s: => %s args=(%s)", castColId.toString(),
        toType.name,
        argsJoin);
  }
}
