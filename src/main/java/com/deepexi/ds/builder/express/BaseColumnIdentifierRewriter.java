package com.deepexi.ds.builder.express;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.expression.ArithmeticExpression;
import com.deepexi.ds.ast.expression.BooleanLiteral;
import com.deepexi.ds.ast.expression.CaseWhenExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression.WhenThen;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.DataTypeLiteral;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.UdfCastExpression;
import com.deepexi.ds.ast.expression.UdfExpression;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ast.window.FrameBoundary;
import com.deepexi.ds.ast.window.Window;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 这个类用于改写 Column中的 identifier(prefix, value). 其中 prefix 表示 表名, value = 字段名
 */
public abstract class BaseColumnIdentifierRewriter implements AstNodeVisitor<AstNode, Void> {

  /**
   * 正常的程序入口
   */
  @Override
  public AstNode visitColumn(Column node, Void context) {
    Window window = node.getWindow();
    Window newWindow = window;
    if (window != null) {
      newWindow = (Window) process(window, context);
    }
    Expression newExpr = (Expression) process(node.getExpr(), context);
    return new Column(node.getAlias(), newExpr, node.getDataType(), node.getDatePart(), newWindow);
  }


  @Override
  public AstNode visitWindow(Window node, Void context) {
    ImmutableList<Identifier> partitions = node.getPartitions();
    List<Identifier> newPartitions = partitions;
    if (partitions.size() > 0) {
      newPartitions = partitions.stream()
          .map(column -> (Identifier) process(column, context))
          .collect(Collectors.toList());
    }
    ImmutableList<OrderBy> orderBys = node.getOrderBys();
    List<OrderBy> newOrderBys = orderBys;
    if (orderBys.size() > 0) {
      newOrderBys = orderBys.stream()
          .map(orderBy -> (OrderBy) process(orderBy, context))
          .collect(Collectors.toList());
    }

    return new Window(
        newPartitions,
        newOrderBys,
        node.getFrameType(),
        node.getFrameStart(),
        node.getFrameEnd());
  }

  @Override
  public AstNode visitOrderBy(OrderBy node, Void context) {
    Identifier identifier = (Identifier) process(node, context);
    return new OrderBy(identifier, node.getDirection());
  }

  @Override
  public AstNode visitCaseWhen(CaseWhenExpression node, Void context) {
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
  public AstNode visitWhenThen(WhenThen node, Void context) {
    Expression when = (Expression) process(node.getWhen(), context);
    Expression then = (Expression) process(node.getThen(), context);
    return new WhenThen(when, then);
  }

  @Override
  public AstNode visitStringLiteral(StringLiteral node, Void context) {
    return node;
  }


  @Override
  public AstNode visitIntegerLiteral(IntegerLiteral node, Void context) {
    return node;
  }

  @Override
  public AstNode visitCompareExpression(CompareExpression node, Void context) {
    Expression left = (Expression) process(node.getLeft(), context);
    Expression right = (Expression) process(node.getRight(), context);
    return node.replaceLeftRight(left, right);
  }

  @Override
  public AstNode visitBooleanLiteral(BooleanLiteral node, Void context) {
    return node;
  }

  @Override
  public AstNode visitFunction(FunctionExpression node, Void context) {
    if (node.getArgs().size() == 0) {
      return node;
    }
    List<Expression> newArgs = new ArrayList<>();
    for (Expression expression : node.getArgs()) {
      Expression newArg = (Expression) process(expression, context);
      newArgs.add(newArg);
    }
    return new FunctionExpression(node.getName(), newArgs);
  }

  @Override
  public AstNode visitUdfExpression(UdfExpression node, Void context) {
    if (node.getArgs().size() == 0) {
      return node;
    }
    List<Expression> newArgs = new ArrayList<>();
    for (Expression expression : node.getArgs()) {
      Expression newArg = (Expression) process(expression, context);
      newArgs.add(newArg);
    }
    return new UdfExpression(node.getName(), newArgs);
  }

  @Override
  public AstNode visitDataTypeLiteral(DataTypeLiteral node, Void context) {
    return node;
  }

  @Override
  public AstNode visitUdfCastExpression(UdfCastExpression node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitArithmeticExpression(ArithmeticExpression node, Void context) {
    Expression left = (Expression) process(node.getLeft(), context);
    Expression right = (Expression) process(node.getRight(), context);
    return new ArithmeticExpression(left, right, node.getOp());
  }

  @Override
  public AstNode visitNode(AstNode node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitModel(Model node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitJoin(Join node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitExpression(Expression node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitTableSource(TableSource node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitModelSource(ModelSource node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitMetricBindQuery(MetricBindQuery node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public AstNode visitFrameBoundary(FrameBoundary node, Void context) {
    throw new RuntimeException("should not be visit");
  }
}
