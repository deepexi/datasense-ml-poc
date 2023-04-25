//package com.deepexi.ds.ast.visitor.analyzer;
//
//import com.deepexi.ds.ast.Column;
//import com.deepexi.ds.ast.Dimension;
//import com.deepexi.ds.ast.Join;
//import com.deepexi.ds.ModelException;
//import com.deepexi.ds.ast.Model;
//import com.deepexi.ds.ast.expression.Expression;
//import com.deepexi.ds.ast.expression.Identifier;
//import com.deepexi.ds.ast.expression.IntegerLiteral;
//import com.deepexi.ds.ast.expression.StringLiteral;
//import com.deepexi.ds.ast.source.ModelSource;
//import com.deepexi.ds.ast.source.Source;
//import com.deepexi.ds.ast.source.TableSource;
//import com.deepexi.ds.ast.ModelVisitor;
//
///**
// * visit node=[source, join], get all available scope
// */
//public class ScopeBinder implements ModelVisitor<Void, ScopeBinderContext> {
//
//  @Override
//  public Void visitModel(Model node, ScopeBinderContext context) {
//    Source source = node.getSource();
//
//    // name: no need bind
//    // Identifier identifier = node.getName();
//    // context.bindScope(identifier, context.getScope(source));
//
//    // source: no need bind
//    // context.bindScope(source, context.getScope(source));
//    // process(source, context);
//
//    // joins
//    if (node.getJoins() != null) {
//      node.getJoins().forEach(join -> process(join, context));
//    }
//
//    // columns
//    if (node.getColumns() != null) {
//      node.getColumns().forEach(column -> process(column, context));
//    }
//
//    // dimensions
//    if (node.getDimensions() != null) {
//      node.getDimensions().forEach(dim -> process(dim, context));
//    }
//    return null;
//  }
//
//  @Override
//  public Void visitColumn(Column node, ScopeBinderContext context) {
//    // process(node.getAlias(), context);
//    // since alias is string, don't need process
//    process(node.getExpr(), context);
//    return null;
//  }
//
//  @Override
//  public Void visitJoin(Join node, ScopeBinderContext context) {
//    Identifier modelName = node.getModel().getName();
//    context.bindScope(modelName, context.getAllScope());
//
//    if (node.getConditions() != null) {
//      node.getConditions().forEach(expression -> process(expression, context));
//    }
//
//    return null;
//  }
//
//  @Override
//  public Void visitDimension(Dimension node, ScopeBinderContext context) {
//    // process(node.getName(), context);
//    process(node.getExpr(), context);
//    return null;
//  }
//
//  @Override
//  public Void visitExpression(Expression node, ScopeBinderContext context) {
//    throw new ModelException("this node should not be visit");
//  }
//
//  @Override
//  public Void visitSource(Source node, ScopeBinderContext context) {
//    throw new ModelException("this node should not be visit");
//  }
//
//  @Override
//  public Void visitTableSource(TableSource node, ScopeBinderContext context) {
//    throw new ModelException("this node should not be visit");
//  }
//
//  @Override
//  public Void visitModelSource(ModelSource node, ScopeBinderContext context) {
//    throw new ModelException("this node should not be visit");
//  }
//
//  @Override
//  public Void visitStringLiteral(StringLiteral node, ScopeBinderContext context) {
//    context.bindScope(node, context.getAllScope());
//    return null;
//  }
//
//  @Override
//  public Void visitIntegerLiteral(IntegerLiteral node, ScopeBinderContext context) {
//    context.bindScope(node, context.getAllScope());
//    return null;
//  }
//
//  @Override
//  public Void visitCompareExpression(CompareExpression node, ScopeBinderContext context) {
//    process(node.getLeft(), context);
//    process(node.getRight(), context);
//    return null;
//  }
//
//  @Override
//  public Void visitIdentifier(Identifier node, ScopeBinderContext context) {
//    context.bindScope(node, context.getAllScope());
//    return null;
//  }
//
//
//}
