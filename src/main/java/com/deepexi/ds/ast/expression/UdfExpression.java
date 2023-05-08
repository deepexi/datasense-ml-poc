package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNodeVisitor;
import java.util.Arrays;
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

  public static UdfExpression BASE_DATE_19700101 = new UdfExpression(
      "create_date_by_ymd",
      Arrays.asList(
          new IntegerLiteral(1970),
          new IntegerLiteral(1),
          new IntegerLiteral(1)
      )
  );
}
