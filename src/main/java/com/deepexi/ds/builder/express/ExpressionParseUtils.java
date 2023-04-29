package com.deepexi.ds.builder.express;


import com.deepexi.ds.ast.expression.CompareOperator;

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

  public static CompareOperator extractBinaryOperator(String literal) {
    if (literal.contains(CompareOperator.GTE.name)) {
      return CompareOperator.GTE;
    }
    if (literal.contains(CompareOperator.LTE.name)) {
      return CompareOperator.LTE;
    }
    if (literal.contains(CompareOperator.NOT_EQUAL.name)) {
      return CompareOperator.NOT_EQUAL;
    }

    // 多字母匹配放在前面, 少的放在后面
    if (literal.contains(CompareOperator.GT.name)) {
      return CompareOperator.GT;
    }
    if (literal.contains(CompareOperator.LT.name)) {
      return CompareOperator.LT;
    }
    if (literal.contains(CompareOperator.EQUAL.name)) {
      return CompareOperator.EQUAL;
    }

    return null;
  }
}
