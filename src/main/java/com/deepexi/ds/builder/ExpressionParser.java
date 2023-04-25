package com.deepexi.ds.builder;

import static com.deepexi.ds.ast.expression.Identifier.IDENTIFIER_SEPARATOR;
import static com.deepexi.ds.ast.expression.Identifier.RE_IDENTIFIER_SEPARATOR;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.condition.BinaryExpression;
import com.deepexi.ds.ast.expression.condition.BinaryExpression.BinaryOperator;
import java.util.List;

/**
 * 待优化, 表达式非常灵活, 暂时做不到很好的分析
 */
class ExpressionParser {

  private final String literal;
  private final List<RelationMock> scope;
  private final RelationMock sourceRelation;

  ExpressionParser(String literal, List<RelationMock> scope, RelationMock sourceRelation) {
    this.literal = literal.trim();
    this.scope = scope;
    this.sourceRelation = sourceRelation;
  }

  Expression parse() {
    return tryParse();
  }

  private Expression tryParse() {
    // 2元运算符:
    //    [tblA.]colX  =/<>/!=/>/>=/</<= [tblB].colY
    //    [tblA.]colX >/.... [常量]
    // 一元运算符
    // not in / any / all
    // 其他函数, 如 abs(tblA.colX) > abs(tblB.colY)
    if (isFunction(literal)) {
      // TODO
      return StringLiteral.of(literal);
    }

    BinaryOperator op = parseBinary(literal);
    if (op != null) {
      String[] parts = literal.split(op.name);
      if (parts.length != 2) {
        return StringLiteral.of(literal);
        // TODO
      }
      Expression left = parseLiteral(parts[0].trim());
      Expression right = parseLiteral(parts[1].trim());
      return new BinaryExpression(left, right, op);
    }

    // throw new ModelException("TODO not support yet: condition");
    return StringLiteral.of(literal);
  }

  private Expression parseLiteral(String literal) {
    literal = literal.trim();
    if (literal.contains(IDENTIFIER_SEPARATOR)) {
      String[] parts = literal.split(RE_IDENTIFIER_SEPARATOR);
      String tableName = parts[0].trim();
      String colName = parts[1].trim();

      // check tableName, tupleName legal
      RelationMock rel = AstModelBuilder.assertTableExists(tableName, scope);
      AstModelBuilder.assertColumnExistsInRelation(colName, rel);

      // 校验该字段 存在于此表中
      return new Identifier(tableName, colName);
    }

    // 是否是常量
    if (isNumeric(literal)) {
      return StringLiteral.of(literal);
      // TODO
    }
    if (literal.startsWith("'") && literal.endsWith("'")) {
      return new StringLiteral(literal);
    }

    // this is a colX, check colX exists in defTable
    AstModelBuilder.assertColumnExistsInRelation(literal, sourceRelation);
    return new Identifier(sourceRelation.getTableName().getValue(), literal);
  }


  private BinaryOperator parseBinary(String literal) {
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

  private static boolean isNumeric(String strNum) {
    if (strNum == null) {
      return false;
    }
    try {
      double d = Double.parseDouble(strNum);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  private boolean isFunction(String literal) {
    final String Parentheses = "(";
    // TODO
    return false;
  }
}
