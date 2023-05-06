package com.deepexi.ds.ast;

import com.deepexi.ds.ModelException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public enum ColumnDataType {
  BOOL("bool", Boolean.class),
  DATETIME("datetime", Date.class),
  DATE("date", Date.class),
  DECIMAL("decimal", BigDecimal.class),
  INTEGER("int", Integer.class),
  STRING("string", String.class),
  TIME("time", String.class),
  TIMESTAMP("timestamp", Timestamp.class),
  ;

  public final String name;
  public final Class<?> clazz;

  ColumnDataType(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
  }

  public static String allValues() {
    return Arrays.stream(ColumnDataType.values())
        .map(dt -> dt.name)
        .collect(Collectors.joining(","));
  }

  public static ColumnDataType fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    ColumnDataType dataType = Arrays.stream(ColumnDataType.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
    if (dataType == null) {
      throw new ModelException("cannot recognize dataType:" + name);
    }
    return dataType;
  }
}
