package com.deepexi.ds.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.ast.expression.ArithmeticExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression.WhenThen;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import org.junit.jupiter.api.Test;

public class DsVisitor4ExpressionTest {

  @Test
  public void testParse_simple_arithmetic_1() {
    Expression expr = testStandalone("1 + 2");
    assertTrue(expr instanceof ArithmeticExpression);
    assertEquals("1", ((ArithmeticExpression) expr).getLeft().toString());
    assertEquals("2", ((ArithmeticExpression) expr).getRight().toString());
    assertEquals("+", ((ArithmeticExpression) expr).getOp().getName());

    assertEquals("1+2", testStandalone("1 + 2").toString());
    assertEquals("1-2", testStandalone(" 1 -    2 ").toString());
    assertEquals("11*3", testStandalone("11 * 3").toString());
    assertEquals("21/4", testStandalone(" 21 / 4 ").toString());
    assertEquals("212%5", testStandalone(" 212 % 5").toString());
  }

  @Test
  public void testParse_simple_arithmetic_2() {
    assertEquals("-1+8", testStandalone(" -1 + 8").toString());
    assertEquals("-1*-9", testStandalone(" -1 * -9   ").toString());
    assertEquals("(-1*2)+(4/2)", testStandalone("-1 * 2 + 4 / 2 ").toString());
    assertEquals("(-1*(2+4))/2", testStandalone("-1 * (2 + 4) / 2 ").toString());
    //    assertEquals(xxx, testBoolean(xxx).toString());
    //    assertEquals(xxx, testBoolean(xxx).toString());
  }


  @Test
  public void testParse_simple_boolean_1() {
    Expression expr = testBoolean(" T1.C1=1 ");
    assertTrue(expr instanceof CompareExpression);
    assertEquals("T1.C1", ((CompareExpression) expr).getLeft().toString());
    assertTrue(((CompareExpression) expr).getLeft() instanceof Identifier);
    assertEquals("1", ((CompareExpression) expr).getRight().toString());
    assertEquals("=", ((CompareExpression) expr).getOp().getName());

    assertEquals("T1.C1>1", testBoolean("T1.C1 >1").toString());
    assertEquals("T1.C1>1", testBoolean("T1.C1 >1").toString());
    assertEquals("T1.C1<1", testBoolean(" T1.C1< 1").toString());
    assertEquals("T1.C1>=1", testBoolean("T1.C1 >= 1").toString());
    assertEquals("T1.C1<=1", testBoolean("T1.C1 <= 1").toString());
    assertEquals("T1.C1<>1", testBoolean("T1.C1 <> 1").toString());
    assertEquals("T1.C1=T2.C2", testBoolean("T1.C1 =T2.C2").toString());
    //    assertEquals(xxx, testBoolean(xxx).toString());
    //    assertEquals(xxx, testBoolean(xxx).toString());
  }


  @Test
  public void testParse_standalone_function() {
    Expression expr = testStandalone("MAX(888,999)");
    assertTrue(expr instanceof FunctionExpression);
    assertEquals("MAX", ((FunctionExpression) expr).getName());
    assertEquals(2, ((FunctionExpression) expr).getArgs().size());
    assertTrue(((FunctionExpression) expr).getArgs().get(0) instanceof IntegerLiteral);
    assertEquals(888, ((IntegerLiteral) ((FunctionExpression) expr).getArgs().get(0)).getValue());
    assertEquals(999, ((IntegerLiteral) ((FunctionExpression) expr).getArgs().get(1)).getValue());

    assertEquals("MAX(1,2)", testStandalone("MAX(1,   2)").toString());
    assertEquals("FUN1(T1.C1,T2,C2)", testStandalone("FUN1(T1.C1, T2,C2)").toString());
    assertEquals("FUN1(T1.C1,'T2'.C2)", testStandalone("FUN1(T1.C1, 'T2'.C2)").toString());
    assertEquals("FUN1(T1.C1,'T2'.C2,'T3')",
        testStandalone("FUN1(T1.C1, 'T2'.C2, 'T3')").toString());
    assertEquals("COUNT(*)", testStandalone("COUNT(*)").toString());
    assertEquals("MAX(1+2,T1.C1)", testStandalone("MAX(1+2, T1.C1)").toString());

    assertEquals("MAX(COUNT(*),FUN2(C1,C2))",
        testStandalone("MAX(COUNT(*), FUN2(C1,C2))").toString());
    //    assertEquals(xxx, testStandalone(xxx).toString());
    //    assertEquals(xxx, testStandalone(xxx).toString());
  }

  @Test
  public void testParse_case_when_1() {
    String s = "case when d_dom <= 3 then xxx when d_dom <=4 then yyy else zzz end";
    Expression expr = testStandalone(s);
    assertTrue(expr instanceof CaseWhenExpression);
    CaseWhenExpression caseWhen = (CaseWhenExpression) expr;

    assertEquals(2, caseWhen.getWhenThenList().size());
    WhenThen whenThen0 = caseWhen.getWhenThenList().get(0);
    assertTrue(whenThen0.getWhen() instanceof CompareExpression);
    assertEquals("d_dom", ((CompareExpression) whenThen0.getWhen()).getLeft().toString());
    assertEquals("<=", ((CompareExpression) whenThen0.getWhen()).getOp().getName());
    assertEquals("3", ((CompareExpression) whenThen0.getWhen()).getRight().toString());
    assertEquals("xxx", whenThen0.getThen().toString());


    WhenThen whenThen1 = caseWhen.getWhenThenList().get(1);
    assertTrue(whenThen1.getWhen() instanceof CompareExpression);
    assertEquals("d_dom", ((CompareExpression) whenThen1.getWhen()).getLeft().toString());
    assertEquals("<=", ((CompareExpression) whenThen1.getWhen()).getOp().getName());
    assertEquals("4", ((CompareExpression) whenThen1.getWhen()).getRight().toString());
    assertEquals("yyy", whenThen1.getThen().toString());

    assertNotNull(caseWhen.getElseExpression());
    assertEquals("zzz", caseWhen.getElseExpression().toString());
  }

  @Test
  public void testParse_case_when_no_else() {
    String s = "case when d_dom <= 3 then xxx when d_dom <=4 then yyy end";
    Expression expr = testStandalone(s);
    assertTrue(expr instanceof CaseWhenExpression);
    CaseWhenExpression caseWhen = (CaseWhenExpression) expr;

    assertNull(caseWhen.getElseExpression());
  }

  private static Expression testStandalone(String inExpr) {
    return ParserUtils.parseStandaloneExpression(inExpr);
  }

  private static Expression testBoolean(String inExpr) {
    return ParserUtils.parseBooleanExpression(inExpr);
  }
}
