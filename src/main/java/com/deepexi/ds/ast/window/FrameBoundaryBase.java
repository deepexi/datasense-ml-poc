package com.deepexi.ds.ast.window;

import java.util.Arrays;
import java.util.Objects;


public enum FrameBoundaryBase {
  UNBOUNDED_PRECEDING("unbounded_preceding"),
  N_PRECEDING("n_preceding"),
  CURRENT_ROW("current_row"),
  N_FOLLOWING("n_following"),
  UNBOUNDED_FOLLOWING("unbounded_following"),
  ;

  public final String name;

  FrameBoundaryBase(String name) {
    this.name = name;
  }

  public static FrameBoundaryBase fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(FrameBoundaryBase.values())
        .filter(ele -> Objects.equals(ele.name.toLowerCase(), lowerName))
        .findAny().orElse(null);
  }
}