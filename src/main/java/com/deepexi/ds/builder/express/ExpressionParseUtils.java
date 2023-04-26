package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.condition.BinaryExpression;
import com.deepexi.ds.ast.expression.condition.BinaryExpression.BinaryOperator;

public class ExpressionParseUtils {

//  public BinaryOperator tryParseAsBinaryOperator(String in) {
//    BinaryOperator op = extractBinaryOperator(in);
//    if (op == null) {
//      return null;
//    }
//
//    String[] parts = in.split(op.name);
//    if (parts.length != 2) {
//      return StringLiteral.of(in);
//      // TODO
//    }
//    Expression left = parseLiteral(parts[0].trim());
//    Expression right = parseLiteral(parts[1].trim());
//    return new BinaryExpression(left, right, op);
//
//    throw new TODOException("TODO");
//  }

  public static BinaryOperator extractBinaryOperator(String literal) {
    if (literal.contains(BinaryOperator.GTE.name)) {
      return BinaryOperator.GTE;
    }
    if (literal.contains(BinaryOperator.LTE.name)) {
      return BinaryOperator.LTE;
    }
    if (literal.contains(BinaryOperator.NOT_EQUAL.name)) {
      return BinaryOperator.NOT_EQUAL;
    }

    // 多字母匹配放在前面, 少的放在后面
    if (literal.contains(BinaryOperator.GT.name)) {
      return BinaryOperator.GT;
    }
    if (literal.contains(BinaryOperator.LT.name)) {
      return BinaryOperator.LT;
    }
    if (literal.contains(BinaryOperator.EQUAL.name)) {
      return BinaryOperator.EQUAL;
    }

    return null;
  }
}
