package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.Relation;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类用于改写 Column中的 identifier(prefix, value). 其中 prefix 表示 表名, value = 字段名
 */
public class BaseColumnIdentifierChecker implements AstNodeVisitor<Void, Void> {

  private final Relation r;
  private final List<String> allTableName = new ArrayList<>();

  public BaseColumnIdentifierChecker(Relation r) {
    this.r = r;
    allTableName.add(r.getFrom().getTableName().getValue());
    r.getJoins().forEach(j -> allTableName.add(j.getModel().getTableName().getValue()));
  }

  @Override
  public Void visitModel(Model node, Void context) {
    node.getJoins().forEach(j -> process(j, context));
    node.getColumns().forEach(c -> process(c, context));
    // node.getDimensions().forEach(c -> process(c, context));
    node.getOrderBys().forEach(o -> process(o.getName(), context));
    return null;
  }

  @Override
  public Void visitColumn(Column node, Void context) {
    process(node.getExpr(), context);
    if (node.getWindow() != null) {
      process(node.getWindow(), context);
    }
    return null;
  }


  @Override
  public Void visitWindow(Window node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitOrderBy(OrderBy node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitCaseWhen(CaseWhenExpression node, Void context) {
    node.getWhenThenList().forEach(whenThen -> process(whenThen, context));
    if (node.getElseExpression() != null) {
      process(node.getElseExpression(), context);
    }
    return null;
  }

  @Override
  public Void visitWhenThen(WhenThen node, Void context) {
    process(node.getWhen(), context);
    process(node.getThen(), context);
    return null;
  }

  @Override
  public Void visitStringLiteral(StringLiteral node, Void context) {
    return null;
  }

  @Override
  public Void visitIdentifier(Identifier node, Void context) {
    String prefix = node.getPrefix();
    if (prefix != null && !allTableName.contains(prefix)) {
      throw new ModelException("cannot access table:" + prefix);
    }
    return null;
  }


  @Override
  public Void visitIntegerLiteral(IntegerLiteral node, Void context) {
    return null;
  }

  @Override
  public Void visitCompareExpression(CompareExpression node, Void context) {
    process(node.getLeft(), context);
    process(node.getRight(), context);
    return null;
  }

  @Override
  public Void visitBooleanLiteral(BooleanLiteral node, Void context) {
    return null;
  }

  @Override
  public Void visitFunction(FunctionExpression node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitUdfExpression(UdfExpression node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitDataTypeLiteral(DataTypeLiteral node, Void context) {
    return null;
  }

  @Override
  public Void visitUdfCastExpression(UdfCastExpression node, Void context) {
    process(node.getCastWhat(), context);
    node.getCastArgs().forEach(e -> process(e, context));
    return null;
  }

  @Override
  public Void visitArithmeticExpression(ArithmeticExpression node, Void context) {
    process(node.getLeft(), context);
    process(node.getLeft(), context);
    return null;
  }

  @Override
  public Void visitNode(AstNode node, Void context) {
    throw new RuntimeException("should not be visit");
  }


  @Override
  public Void visitJoin(Join node, Void context) {
    node.getConditions().forEach(e -> process(e, context));
    return null;
  }

  @Override
  public Void visitExpression(Expression node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitTableSource(TableSource node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitModelSource(ModelSource node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitMetricBindQuery(MetricQuery node, Void context) {
    throw new RuntimeException("should not be visit");
  }

  @Override
  public Void visitFrameBoundary(FrameBoundary node, Void context) {
    throw new RuntimeException("should not be visit");
  }
}
