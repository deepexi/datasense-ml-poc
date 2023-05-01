package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;

public class ExpressionRewriter implements AstNodeVisitor<Expression, Void> {

  @Override
  public Expression visitNode(AstNode node, Void context) {
    return AstNodeVisitor.super.visitNode(node, context);
  }

  @Override
  public Expression visitModel(Model node, Void context) {
    return null;
  }

  @Override
  public Expression visitColumn(Column node, Void context) {
    return null;
  }

  @Override
  public Expression visitJoin(Join node, Void context) {
    return null;
  }

  @Override
  public Expression visitExpression(Expression node, Void context) {
    return null;
  }

  @Override
  public Expression visitTableSource(TableSource node, Void context) {
    return null;
  }

  @Override
  public Expression visitModelSource(ModelSource node, Void context) {
    return null;
  }

  @Override
  public Expression visitStringLiteral(StringLiteral node, Void context) {
    return null;
  }

  @Override
  public Expression visitIdentifier(Identifier node, Void context) {
    return null;
  }

  @Override
  public Expression visitIntegerLiteral(IntegerLiteral node, Void context) {
    return null;
  }

  @Override
  public Expression visitCompareExpression(CompareExpression node, Void context) {
    return null;
  }

  @Override
  public Expression visitMetricBindQuery(MetricBindQuery node, Void context) {
    return null;
  }

  @Override
  public Expression visitOrderBy(OrderBy node, Void context) {
    return null;
  }
}
