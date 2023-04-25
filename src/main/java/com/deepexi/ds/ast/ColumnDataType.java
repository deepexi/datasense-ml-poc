package com.deepexi.ds.ast;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

public enum ColumnDataType {
  STRING("string", String.class),
  INTEGER("int", Integer.class),
  DECIMAL("decimal", BigDecimal.class),
  DATE("date", Date.class),
  Timestamp("timestamp", Timestamp.class);

  public final String name;
  public final Class<?> clazz;

  ColumnDataType(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public static ColumnDataType fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(ColumnDataType.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }
}
