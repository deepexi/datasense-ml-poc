package com.deepexi.ds.parser;

import com.deepexi.ds.antlr4.DsLexer;
import com.deepexi.ds.antlr4.DsParser;
import com.deepexi.ds.antlr4.DsParser.BooleanExpressionContext;
import com.deepexi.ds.antlr4.DsParser.StandaloneExpressionContext;
import com.deepexi.ds.ast.expression.Expression;
import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

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
      "-1 * (2 + 4) / 2 "
  );

  public static List<String> case_boolean = Arrays.asList(
      " T1.C1=1 ",
      "T1.C1 >1",
      " T1.C1< 1",
      "T1.C1 >= 1",
      "T1.C1 <= 1",
      "T1.C1 <> 1",
      "T1.C1 =T2.C2"
  );

  public static void main(String[] args) {
    // ========  boolean ========
    // testOneExpr(case_boolean.get(1));
    // all
    case_boolean.forEach(DsVisitorTest::testBoolean);
    // last
    // testBoolean(case_boolean.get(case_boolean.size() - 1));

    // ========  standalone ========
    //testStandalone(case_standalone.get(case_standalone.size() - 1));
    //case_standalone.forEach(DsVisitorTest::testStandalone);
  }

  private static void testStandalone(String inExpr) {
    String expr = inExpr.toUpperCase();
    CharStream input = CharStreams.fromString(expr);
    DsLexer lexer = new DsLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DsParser parser = new DsParser(tokens);

    StandaloneExpressionContext tree = parser.standaloneExpression();
    DsVisitor4Expression tv = new DsVisitor4Expression();
    Expression expression = tv.visit(tree);
    System.out.println(expression.toString());
  }

  private static void testBoolean(String inExpr) {
    String expr = inExpr.toUpperCase();
    CharStream input = CharStreams.fromString(expr);
    DsLexer lexer = new DsLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DsParser parser = new DsParser(tokens);

    BooleanExpressionContext tree = parser.booleanExpression();
    DsVisitor4Expression tv = new DsVisitor4Expression();
    Expression expression = tv.visit(tree);
    System.out.println(expression.toString());
  }
}
