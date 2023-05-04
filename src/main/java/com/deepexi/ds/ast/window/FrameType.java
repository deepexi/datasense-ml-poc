package com.deepexi.ds.ast.window;

import java.util.Arrays;
import java.util.Objects;

public enum FrameType {
  ROWS("rows"),
  RANGE("range"),
  GROUPS("groups"),
  ;

  public final String name;

  FrameType(String name) {
    this.name = name;
  }

  public static FrameType fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(FrameType.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }
}