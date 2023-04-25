//package com.deepexi.ds.ast;
//
//
//import com.deepexi.ds.ast.expression.Expression;
//import com.deepexi.ds.ast.expression.Identifier;
//import com.google.common.collect.ImmutableList;
//import lombok.Getter;
//
//@Getter
//public class Metric extends AstComponent {
//
//  private final Identifier name;
//  private final Model model;
//  private final ImmutableList<Dimension> dimensions;
//  private final Expression aggregate;
//  private final Column output;
//  private final Expression where;
//
//  public Metric(Identifier name, Model model, ImmutableList<Dimension> dimensions,
//      Expression aggregate, Column output, Expression where) {
//    this.name = name;
//    this.model = model;
//    this.dimensions = dimensions;
//    this.aggregate = aggregate;
//    this.output = output;
//    this.where = where;
//  }
//
//  @Override
//  public ComponentType getComponentType() {
//    return ComponentType.METRICS_ML;
//  }
//
//  @Override
//  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
//    return null;
//  }
//}
