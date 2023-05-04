package com.deepexi.ds.builder.express;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.expression.BooleanLiteral;
import com.deepexi.ds.ast.expression.CaseWhenExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression.WhenThen;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.builder.express.AddTableNameToColumnRewriter.AvailTableContext;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * col加上 表名 colA => tableX.colA
 */
public class AddTableNameToColumnRewriter implements AstNodeVisitor<AstNode, AvailTableContext> {

  @Getter
  public static class AvailTableContext {

    private final Identifier sourceRelationIdentifier;

    public AvailTableContext(Identifier identifier) {
      this.sourceRelationIdentifier = identifier;
    }
  }

  /**
   * 正常的程序入口
   */
  @Override
  public AstNode visitColumn(Column node, AvailTableContext context) {
    Expression newExpr = (Expression) process(node.getExpr(), context);
    return node.replaceExpr(newExpr);
  }

  @Override
  public AstNode visitIdentifier(Identifier node, AvailTableContext context) {
    if (node.getPrefix() != null) {
      return node;
    }
    return node.replacePrefix(context.sourceRelationIdentifier.getValue());
  }

  @Override
  public AstNode visitCaseWhen(CaseWhenExpression node, AvailTableContext context) {
    List<WhenThen> collect = new ArrayList<>();
    for (WhenThen whenThen : node.getWhenThenList()) {
      Expression process = (Expression) process(whenThen, context);
      collect.add((WhenThen) process);
    }
    Expression elseExpression = null;
    if (node.getElseExpression() != null) {
      elseExpression = (Expression) process(node.getElseExpression(), context);
    }
    return new CaseWhenExpression(collect, elseExpression);
  }

  @Override
  public AstNode visitWhenThen(WhenThen node, AvailTableContext context) {
    Expression when = (Expression) process(node.getWhen(), context);
    Expression then = (Expression) process(node.getThen(), context);
    return new WhenThen(when, then);
  }

  @Override
  public AstNode visitStringLiteral(StringLiteral node, AvailTableContext context) {
    return node;
  }


  @Override
  public AstNode visitIntegerLiteral(IntegerLiteral node, AvailTableContext context) {
    return node;
  }

  @Override
  public AstNode visitCompareExpression(CompareExpression node, AvailTableContext context) {
    Expression left = (Expression) process(node.getLeft(), context);
    Expression right = (Expression) process(node.getRight(), context);
    return node.replaceLeftRight(left, right);
  }

  @Override
  public AstNode visitBooleanLiteral(BooleanLiteral node, AvailTableContext context) {
    return node;
  }

  @Override
  public AstNode visitFunction(FunctionExpression node, AvailTableContext context) {
    if (node.getArgs().size() == 0) {
      return node;
    }
    List<Expression> newArgs = new ArrayList<>();
    for (Expression expression : node.getArgs()) {
      Expression newArg = (Expression) process(expression, context);
      newArgs.add(newArg);
    }
    return node.replaceArgs(newArgs);
  }

  @Override
  public AstNode visitNode(AstNode node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitModel(Model node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitJoin(Join node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitExpression(Expression node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitTableSource(TableSource node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitModelSource(ModelSource node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitMetricBindQuery(MetricBindQuery node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitOrderBy(OrderBy node, AvailTableContext context) {
    throw new RuntimeException("should not be visit");
  }

}
