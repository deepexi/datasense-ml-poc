package com.deepexi.ds.ast.window;

import java.util.Arrays;
import java.util.Objects;

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