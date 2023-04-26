package com.deepexi.ds.ast.visitor.analyzer;

import com.deepexi.ds.ModelException.UnsupportedException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Dimension;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.ModelVisitor;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.RelationFromModel;
import com.deepexi.ds.ast.RelationFromModelSource;
import com.deepexi.ds.ast.RelationFromTableSource;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.condition.BinaryExpression;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.Source;
import com.deepexi.ds.ast.source.TableSource;

/**
 * visit node=[source, join], get all available scope
 */
public class ScopeCollector implements ModelVisitor<Void, ScopeCollectorContext> {

  @Override
  public Void visitModel(Model node, ScopeCollectorContext context) {
    // visit source and joins
    process(node.getSource(), context);

    if (node.getJoins() != null && node.getJoins().size() > 0) {
      node.getJoins().forEach(join -> process(join, context));
    }

    return null;
  }

  @Override
  public Void visitTableSource(TableSource node, ScopeCollectorContext context) {
    Relation tableLike = new RelationFromTableSource(node, context);
    context.registerTableLike(node, tableLike);
    context.registerSourceTableLike(tableLike);
    return null;
  }

  @Override
  public Void visitModelSource(ModelSource node, ScopeCollectorContext context) {
    Relation tableLike = new RelationFromModelSource(node, context);
    context.registerTableLike(node, tableLike);
    context.registerSourceTableLike(tableLike);

    // 递归处理
    ScopeCollectorContext subContext = new ScopeCollectorContext(node.getModel());
    process(node.getModel(), subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitRelationFromModel(RelationFromModel node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitRelationFromModelSource(RelationFromModelSource node,
      ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitJoin(Join node, ScopeCollectorContext context) {
    Model joinModel = node.getModel();
    context.registerTableLike(node, new RelationFromModel(joinModel, context));

    // 递归处理
    ScopeCollectorContext subContext = new ScopeCollectorContext(node.getModel());
    process(node.getModel(), subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitRelationFromTableSource(RelationFromTableSource node,
      ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitMetricBindQuery(MetricBindQuery node, ScopeCollectorContext context) {
    Model model = node.getModel();
    ScopeCollectorContext subContext = new ScopeCollectorContext(model);
    process(model, subContext);
    // 处理完毕收集
    context.getRegistry().putAll(subContext.getRegistry());
    return null;
  }

  @Override
  public Void visitColumn(Column node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitDimension(Dimension node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitExpression(Expression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitSource(Source node, ScopeCollectorContext context) {
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
  public Void visitCompareExpression(BinaryExpression node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }

  @Override
  public Void visitIdentifier(Identifier node, ScopeCollectorContext context) {
    throw new UnsupportedException("this node should not be visit");
  }
}
