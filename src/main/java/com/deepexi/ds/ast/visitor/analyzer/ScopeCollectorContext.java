package com.deepexi.ds.ast.visitor.analyzer;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 用于收集 树中的 scope
 */
@Getter
public class ScopeCollectorContext {
  protected final Model root;
  private final Map<ModelNodeRef, Relation> registry = new HashMap<>();
  private Relation sourceTableLike;

  public ScopeCollectorContext(Model root) {
    this.root = root;
  }

  void registerTableLike(AstNode node, Relation tableLike) {
    registry.put(new ModelNodeRef(node), tableLike);
  }

  void registerSourceTableLike(Relation tableLike) {
    this.sourceTableLike = tableLike;
  }
}
