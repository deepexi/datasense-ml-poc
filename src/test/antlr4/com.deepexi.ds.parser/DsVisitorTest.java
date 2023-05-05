package com.deepexi.ds.parser;

import com.deepexi.ds.ast.expression.Expression;
import java.util.Arrays;
import java.util.List;

public class DsVisitorTest {

  public static List<String> case_standalone = Arrays.asList(
      "1 + 2",
      "1 - 2",
      "11 * 3",
      " 21 / 4",
      " 212 % 5",
      " -1 + 8",
      " -1 * -9",
      "-1 * 2 + 4 / 2 ",
      "-1 * (2 + 4) / 2 ",
      // basic function
      "MAX(1,2)",
      "FUN1(T1.C1, T2,C2)",
      "FUN1(T1.C1, 'T2'.C2)",
      "FUN1(T1.C1, 'T2'.C2, 'T3')",
      "COUNT(*)",
      //
      "MAX(1+2, T1.C1)",
      "MAX(COUNT(*), FUN2(C1,C2))",
      //
      "case when d_dom <= 3 then 3 when d_dom <=4 then 4 else 9 end",
      "case when d_dom <= 3 then xxx when d_dom <=4 then yyy end"
  );

  public static List<String> case_boolean = Arrays.asList(
      " T1.C1=1 ",
      "T1.C1 >1",
      " T1.C1< 1",
      "T1.C1 >= 1",
      "T1.C1 <= 1",
      "T1.C1 <> 1",
      "T1.C1 =T2.C2",
      "T1='hello'"
  );

  public static void main(String[] args) {
    // ========  boolean ========
    // testOneExpr(case_boolean.get(1));
    // all
    // case_boolean.forEach(DsVisitorTest::testBoolean);
    // last
    testBoolean(case_boolean.get(case_boolean.size() - 1));

    // ========  standalone ========
    //testStandalone(case_standalone.get(case_standalone.size() - 1));
    //case_standalone.forEach(DsVisitorTest::testStandalone);
  }

  private static void testStandalone(String inExpr) {
    Expression expression = ParserUtils.parseStandaloneExpression(inExpr);
    System.out.println(expression.toString());
  }

  private static void testBoolean(String inExpr) {
    Expression expression = ParserUtils.parseBooleanExpression(inExpr);
    System.out.println(expression.toString());
  }
}
