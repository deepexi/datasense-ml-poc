//package com.deepexi.ds.ast.visitor.analyzer;
//
//import com.deepexi.ds.ast.Relation;
//import com.google.common.collect.ImmutableList;
//import java.util.List;
//import lombok.Getter;
//
///**
// * a scope is bound to a Node, for further analysis
// */
//@Getter
//public class ModelAnalyzerScope {
//
//  private final ImmutableList<Relation> tables;
//  private final Relation relation;
//
//  public ModelAnalyzerScope(List<Relation> tables, Relation relation) {
//    this.tables = ImmutableList.copyOf(tables);
//    this.relation = relation;
//  }
//}