package com.deepexi.ds.ast;

import java.util.Arrays;
import java.util.Objects;

public enum JoinType {
  LEFT("left"),
  INNER("inner"),
  FULL("full");

  public final String name;

  JoinType(String name) {
    this.name = name;
  }

  public static JoinType fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(JoinType.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }
}