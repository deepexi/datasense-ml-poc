package com.deepexi.ds.parser;

import com.deepexi.ds.antlr4.DsLexer;
import com.deepexi.ds.antlr4.DsParser;
import com.deepexi.ds.antlr4.DsParser.BooleanExpressionContext;
import com.deepexi.ds.antlr4.DsParser.StandaloneExpressionContext;
import com.deepexi.ds.ast.expression.Expression;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class ParserUtils {

  public static Expression parseStandaloneExpression(String expr) {
    CharStream input = CharStreams.fromString(expr);
    DsLexer lexer = new DsLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DsParser parser = new DsParser(tokens);

    StandaloneExpressionContext tree = parser.standaloneExpression();
    DsVisitor4Expression visitor = new DsVisitor4Expression();
    return visitor.visit(tree);
  }

  public static Expression parseBooleanExpression(String expr) {
    CharStream input = CharStreams.fromString(expr);
    DsLexer lexer = new DsLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    DsParser parser = new DsParser(tokens);

    BooleanExpressionContext tree = parser.booleanExpression();
    DsVisitor4Expression visitor = new DsVisitor4Expression();
    return visitor.visit(tree);
  }
}
