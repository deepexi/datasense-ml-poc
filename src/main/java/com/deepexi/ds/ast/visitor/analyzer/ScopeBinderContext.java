//package com.deepexi.ds.ast.visitor.analyzer;
//
//import com.deepexi.ds.ast.AstNode;
//import com.deepexi.ds.ast.BasicContext;
//import com.deepexi.ds.ast.Model;
//import com.deepexi.ds.ast.Relation;
//import com.google.common.collect.ImmutableMap;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.Getter;
//
//@Getter
//public class ScopeBinderContext extends BasicContext {
//
//  private final ImmutableMap<ModelNodeRef, Relation> registry;
//  private final ModelAnalyzerScope allScope;
//  private final Map<ModelNodeRef, ModelAnalyzerScope> bindingScope = new HashMap<>();
//
//  public ScopeBinderContext(Model root, Map<ModelNodeRef, Relation> registry, Relation relation) {
//    super(root);
//    this.registry = ImmutableMap.copyOf(registry);
//    allScope = new ModelAnalyzerScope(registry.values().stream().toList(), relation);
//  }
//
//  void bindScope(AstNode node, ModelAnalyzerScope scope) {
//    bindingScope.put(ModelNodeRef.of(node), scope);
//  }
//
//}
