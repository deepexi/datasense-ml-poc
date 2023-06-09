package com.deepexi.ds.ast.visitor.analyzer;

import com.deepexi.ds.ModelException.UnsupportedException;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
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

/**
 * visit node=[source, join], get all available scope
 */
public class ScopeCollector implements AstNodeVisitor<Void, ScopeCollectorContext> {

  @Override
  public Void visitModel(Model node, ScopeCollectorContext context) {
    // visit source and joins
    context.registerRelation(node, node);
    process(node.getSource(), context);

    if (node.getJoins() != null && node.getJoins().size() > 0) {
      node.getJoins().forEach(join -> process(join, context));
    }

    return null;
  }

  @Override
  public Void visitModelSource(ModelSource node, ScopeCollectorContext context) {
    Relation relation = node.getModel();
    context.registerRelation(node, relation);
    context.registerSourceRelation(relation);

    // 递归处理
    ScopeCollectorContext subContext = new ScopeCollectorContext(node.getModel());
    process(node.getModel(), subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitTableSource(TableSource node, ScopeCollectorContext context) {
    context.registerRelation(node, node);
    context.registerSourceRelation(node);
    return null;
  }

  @Override
  public Void visitJoin(Join node, ScopeCollectorContext context) {
    context.registerRelation(node, node.getModel());

    // 递归处理
    ScopeCollectorContext subContext = new ScopeCollectorContext(node.getModel());
    process(node.getModel(), subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitMetricBindQuery(MetricBindQuery node, ScopeCollectorContext context) {
    // Model model = node.getModel();
    Relation relation = node.getSource();
    ScopeCollectorContext subContext = new ScopeCollectorContext(relation);
    process(relation, subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitOrderBy(OrderBy node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitCaseWhen(CaseWhenExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitWhenThen(WhenThen node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitBooleanLiteral(BooleanLiteral node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitFunction(FunctionExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitWindow(Window node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitFrameBoundary(FrameBoundary node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitUdfExpression(UdfExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitDataTypeLiteral(DataTypeLiteral node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitUdfCastExpression(UdfCastExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitArithmeticExpression(ArithmeticExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitColumn(Column node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitExpression(Expression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitStringLiteral(StringLiteral stringLiteral, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitIntegerLiteral(IntegerLiteral node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitCompareExpression(CompareExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitIdentifier(Identifier node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }
}
