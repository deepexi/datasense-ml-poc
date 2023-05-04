package com.deepexi.ds.ast.window;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import lombok.Getter;

@Getter
public class FrameBoundary extends AstNode {

  private final FrameBoundaryBase base;
  private final int offset;

  public FrameBoundary(FrameBoundaryBase base, int offset) {
    this.base = base;
    this.offset = offset;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitFrameBoundary(this, context);
  }
}
