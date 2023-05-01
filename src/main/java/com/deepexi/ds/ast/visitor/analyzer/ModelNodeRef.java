package com.deepexi.ds.ast.visitor.analyzer;

import com.deepexi.ds.ast.AstNode;
import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class ModelNodeRef {

  private final AstNode node;

  public ModelNodeRef(AstNode node) {
    this.node = node;
  }

  public static ModelNodeRef of(AstNode node) {
    return new ModelNodeRef(node);
  }

  /**
   * ModelNodeRef是专门设计用来 记录 Node的 内存引用的. 2个 node 直接引用相等
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ModelNodeRef)) {
      return false;
    }
    return this.node == ((ModelNodeRef) o).node; // direct same reference of object
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(node);
  }
}
