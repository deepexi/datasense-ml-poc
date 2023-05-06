package com.deepexi.ds.ast;

import com.deepexi.ds.ModelException.UnsupportedException;
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
import com.deepexi.ds.ast.expression.UdfExpression;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ast.window.FrameBoundary;
import com.deepexi.ds.ast.window.Window;

/**
 * R = Result, C = Context
 */
public interface AstNodeVisitor<R, C> {

  default R process(AstNode node, C context) {
    return node.accept(this, context);
  }

  default R process(AstNode node) {
    return node.accept(this, null);
  }

  default R visitNode(AstNode node, C context) {
    throw new UnsupportedException("should implement in you ModelNode subclass");
  }

  R visitModel(Model node, C context);

  R visitColumn(Column node, C context);

  R visitJoin(Join node, C context);

  R visitExpression(Expression node, C context);

  R visitTableSource(TableSource node, C context);

  R visitModelSource(ModelSource node, C context);

  R visitStringLiteral(StringLiteral node, C context);

  R visitIdentifier(Identifier node, C context);

  R visitIntegerLiteral(IntegerLiteral node, C context);

  R visitCompareExpression(CompareExpression node, C context);

  R visitMetricBindQuery(MetricBindQuery node, C context);

  R visitOrderBy(OrderBy node, C context);

  R visitCaseWhen(CaseWhenExpression node, C context);

  R visitWhenThen(WhenThen node, C context);

  R visitBooleanLiteral(BooleanLiteral node, C context);

  R visitFunction(FunctionExpression node, C context);

  R visitWindow(Window node, C context);

  R visitFrameBoundary(FrameBoundary node, C context);

  R visitUdf(UdfExpression node, C context);

  R visitDataTypeLiteral(DataTypeLiteral node, C context);
}
