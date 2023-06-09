package com.deepexi.ds.ast.window;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class Window extends AstNode {

  private final ImmutableList<Identifier> partitions;   // optional
  private final ImmutableList<OrderBy> orderBys;        // optional
  private final FrameType frameType;                    // rows | range | groups
  private final FrameBoundary frameStart;               // optional
  private final FrameBoundary frameEnd;                 // 与 start 一致

  public Window(
      List<Identifier> partitions,
      List<OrderBy> orderBys,
      FrameType frameType,
      FrameBoundary frameStart,
      FrameBoundary frameEnd) {
    this.partitions = ImmutableList.copyOf(partitions);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.frameType = frameType;
    this.frameStart = frameStart;
    this.frameEnd = frameEnd;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitWindow(this, context);
  }
}
