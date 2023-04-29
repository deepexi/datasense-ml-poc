package com.deepexi.ds.parser;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.antlr4.DsBaseVisitor;
import com.deepexi.ds.antlr4.DsParser;
import com.deepexi.ds.ast.expression.ArithmeticExpression;
import com.deepexi.ds.ast.expression.ArithmeticExpression.ArithmeticOperator;
import com.deepexi.ds.ast.expression.CompareOperator;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import java.math.BigDecimal;
import java.util.Objects;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * 访问节点, 并返回一个 Expression
 */
public class DsVisitor4Expression extends DsBaseVisitor<Expression> {

  private void debug(ParserRuleContext ctx) {
    int childCount = ctx.getChildCount();
    for (int i = 0; i < childCount; i++) {
      System.out.printf("visit %s, child[%s] = %s%n", ctx.getClass().getCanonicalName(), i,
          ctx.getChild(i).getText());
    }
    System.out.println("====================");
  }

  @Override
  public Expression visitStandaloneExpression(DsParser.StandaloneExpressionContext ctx) {
    debug(ctx);
    // child(0) = 有效, child(1) = EOF
    return visit(ctx.getChild(0));
  }

  @Override
  public Expression visitExpression(DsParser.ExpressionContext ctx) {
    debug(ctx);
    // 该节点无需解析
    return visitChildren(ctx);
  }

  @Override
  public Expression visitLogicalNot(DsParser.LogicalNotContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitPredicated(DsParser.PredicatedContext ctx) {
    debug(ctx);
    int childCount = ctx.getChildCount();

    if (childCount == 1) {
      // like  1+2
      //       child0
      return visit(ctx.valueExpression);
    }

    if (childCount == 2) {
      // like t1.c1      =1
      //      child0     child1
      Expression left = visit(ctx.getChild(0));
      CompareExpressionBuilder builder = (CompareExpressionBuilder) visit(ctx.getChild(1));
      return builder.left(left).build();
    }

//    if (childCount == 3) {
//      // like foo=bar
//      Identifier left = (Identifier) visit(ctx.getChild(0));
//      CompareExpressionBuilder builderHasOp = (CompareExpressionBuilder) visit(ctx.getChild(1));
//      builderHasOp.left(left);
//      return builderHasOp.build();
//    }
    throw new TODOException("TODO: can't parse ctx");
  }

  @Override
  public Expression visitOr(DsParser.OrContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitAnd(DsParser.AndContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitComparison(DsParser.ComparisonContext ctx) {
    debug(ctx);
    // here
    String opStr = ctx.getChild(0).getText();
    CompareOperator op = CompareOperator.fromName(opStr);
    Expression right = visit(ctx.getChild(1));
    return new CompareExpressionBuilder().op(op).right(right);
  }

  @Override
  public Expression visitNullPredicate(DsParser.NullPredicateContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitValueExpressionDefault(DsParser.ValueExpressionDefaultContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitConcatenation(DsParser.ConcatenationContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitArithmeticBinary(DsParser.ArithmeticBinaryContext ctx) {
    debug(ctx);
    Expression left = visit(ctx.left);
    Expression right = visit(ctx.right);
    ArithmeticOperator op = ArithmeticOperator.fromName(ctx.operator.getText());
    return new ArithmeticExpression(left, right, op);
  }

  @Override
  public Expression visitArithmeticUnary(DsParser.ArithmeticUnaryContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitDereference(DsParser.DereferenceContext ctx) {
    debug(ctx);
    if (ctx.getChildCount() == 3) {
      String v1 = ctx.getChild(0).getText();
      String v2 = ctx.getChild(1).getText();
      String v3 = ctx.getChild(2).getText();
      if (Objects.equals(v2, ".")) {
        return new Identifier(v1, v3);
      }
    }
    throw new ModelException("can't parse expression");
  }

  @Override
  public Expression visitSimpleCase(DsParser.SimpleCaseContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitColumnReference(DsParser.ColumnReferenceContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitNullLiteral(DsParser.NullLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitParenthesizedExpression(DsParser.ParenthesizedExpressionContext ctx) {
    debug(ctx);
    // ( expr )
    if (ctx.getChildCount() == 3) {
      // only one expr, expr[0]=(,  expr[2]=)
      return visit(ctx.getChild(1));
    }
    throw new RuntimeException("TODO");
  }

  @Override
  public Expression visitStringLiteral(DsParser.StringLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitFunctionCall(DsParser.FunctionCallContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitIntervalLiteral(DsParser.IntervalLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitNumericLiteral(DsParser.NumericLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitBooleanLiteral(DsParser.BooleanLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitBasicStringLiteral(DsParser.BasicStringLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitComparisonOperator(DsParser.ComparisonOperatorContext ctx) {
    debug(ctx);
    String opStr = ctx.getChild(0).getText();
    CompareOperator op = CompareOperator.fromName(opStr);
    return new CompareExpressionBuilder().op(op);
  }

  @Override
  public Expression visitBooleanValue(DsParser.BooleanValueContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitInterval(DsParser.IntervalContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitIntervalField(DsParser.IntervalFieldContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitWhenClause(DsParser.WhenClauseContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitQualifiedName(DsParser.QualifiedNameContext ctx) {
    debug(ctx);
//    int childCount = ctx.getChildCount();
//
//    // case colX
//    if (childCount == 1) {
//      return visitChildren(ctx);
//    }
//
//    // case tableA.colX
//    if (childCount == 3) {
//      String v1 = ctx.getChild(0).getText();
//      String v2 = ctx.getChild(1).getText();
//      String v3 = ctx.getChild(2).getText();
//      if (Objects.equals(v2, ".")) {
//        return new Identifier(v1, v3);
//      }
//    }
//    throw new ModelException("can't parse expression");
    return visitChildren(ctx);
  }

  @Override
  public Expression visitUnquotedIdentifier(DsParser.UnquotedIdentifierContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitQuotedIdentifier(DsParser.QuotedIdentifierContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitBackQuotedIdentifier(DsParser.BackQuotedIdentifierContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitDigitIdentifier(DsParser.DigitIdentifierContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitDecimalLiteral(DsParser.DecimalLiteralContext ctx) {
    debug(ctx);
    String text = ctx.getChild(0).getText();
    BigDecimal bigDecimal = new BigDecimal(text);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitDoubleLiteral(DsParser.DoubleLiteralContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }

  @Override
  public Expression visitIntegerLiteral(DsParser.IntegerLiteralContext ctx) {
    debug(ctx);
    // 可能包含 +/-
    if (ctx.getChildCount() == 1) {
      int value = Integer.parseInt(ctx.getChild(0).getText());
      return IntegerLiteral.of(value);
    }
    if (ctx.getChildCount() == 2) {
      String sign = ctx.getChild(0).getText();
      String num = ctx.getChild(1).getText();
      int value = Integer.parseInt(sign + num);
      return IntegerLiteral.of(value);
    }
    throw new RuntimeException("TODO");
  }

  @Override
  public Expression visitNonReserved(DsParser.NonReservedContext ctx) {
    debug(ctx);
    return visitChildren(ctx);
  }


}