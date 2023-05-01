package com.deepexi.ds.ast.visitor.analyzer;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Relation;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 用于收集 树中的 scope
 */
@Getter
public class ScopeCollectorContext {
  protected final Relation root;
  private final Map<ModelNodeRef, Relation> registry = new HashMap<>();
  private Relation sourceRelation;

  public ScopeCollectorContext(Relation root) {
    this.root = root;
  }

  void registerRelation(AstNode node, Relation relation) {
    registry.put(new ModelNodeRef(node), relation);
  }

  void registerSourceRelation(Relation tableLike) {
    this.sourceRelation = tableLike;
  }
}
