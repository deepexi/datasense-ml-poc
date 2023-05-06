package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import java.util.List;
import lombok.Getter;

@Getter
public class UdfExpression extends FunctionExpression {

  public UdfExpression(String name, List<Expression> args) {
    super(name, args);
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitUdfExpression(this, context);
  }
}
