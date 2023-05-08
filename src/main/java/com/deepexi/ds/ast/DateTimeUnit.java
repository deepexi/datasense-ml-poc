package com.deepexi.ds.ast;

import java.util.Arrays;
import java.util.Objects;

public enum DateTimeUnit {
  DATE("date", 4),            // 到 day
  DATETIME("datetime", 1),    // 到 second
  TIMESTAMP("timestamp", 1),  // 到 second
  //
  YEAR("year", 6),
  MONTH("month", 5),
  DAY("day", 4),
  HOUR("day", 3),
  MINUTE("day", 2),
  SECOND("day", 1),
  ;

  public final String name;
  public final int grainOrder; // 粒度排序, 越大的粒度越粗

  DateTimeUnit(String name, int grainOrder) {
    this.name = name;
    this.grainOrder = grainOrder;
  }

  public static DateTimeUnit fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(DateTimeUnit.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }
}