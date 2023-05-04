//package com.deepexi.ds.ast.window;
//
//import com.deepexi.ds.ast.AstNode;
//import com.deepexi.ds.ast.AstNodeVisitor;
//
//public class Frame extends AstNode {
//  private final FrameType frameType;                    // 计算列
//  private final FrameBoundary frameStart;               // optional
//  private final FrameBoundary frameEnd;                 // 与 start 一致
//
//  public Frame(FrameType frameType, FrameBoundary frameStart, FrameBoundary frameEnd) {
//    this.frameType = frameType;
//    this.frameStart = frameStart;
//    this.frameEnd = frameEnd;
//  }
//
//  @Override
//  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
//    throw new RuntimeException("TODO");
//    return visitor.visitFrame(this, context);
//  }
//}
