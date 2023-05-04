package com.deepexi.ds.ast;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Window extends AstNode {

  private final WindowType windowType;
  private final ImmutableList<Column> partitions;
  private final ImmutableList<OrderBy> orderBys;
  private final WindowBoundary left;
  private final WindowBoundary right;

  public Window(WindowType windowType,
      List<Column> partitions,
      List<OrderBy> orderBys,
      WindowBoundary left,
      WindowBoundary right) {
    this.windowType = windowType;
    this.partitions = ImmutableList.copyOf(partitions);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.left = left;
    this.right = right;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitWindow(this, context);
  }

  @Getter
  public static class WindowBoundary extends AstNode {

    private final WindowBoundaryBase base;
    private final int offset;

    public WindowBoundary(WindowBoundaryBase base, int offset) {
      this.base = base;
      this.offset = offset;
    }

    @Override
    public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
      throw new RuntimeException("TODO");
    }
  }

  public enum WindowBoundaryBase {
    UNBOUNDED("unbounded"),
    CURRENT_ROW("current_row"),
    ;

    public final String name;

    WindowBoundaryBase(String name) {
      this.name = name;
    }

    public static WindowBoundaryBase fromName(String name) {
      if (name == null) {
        return null;
      }
      String lowerName = name.toLowerCase();
      return Arrays.stream(WindowBoundaryBase.values())
          .filter(x -> Objects.equals(x.name, lowerName))
          .findAny().orElse(null);
    }
  }

  public enum WindowType {
    SLIDING("sliding"),
    TUMBLING("tumbling"),
    ;

    public final String name;

    WindowType(String name) {
      this.name = name;
    }

    public static WindowType fromName(String name) {
      if (name == null) {
        return null;
      }
      String lowerName = name.toLowerCase();
      return Arrays.stream(WindowType.values())
          .filter(x -> Objects.equals(x.name, lowerName))
          .findAny().orElse(null);
    }
  }
}
