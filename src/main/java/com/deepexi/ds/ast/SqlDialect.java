package com.deepexi.ds.ast;

import java.util.Arrays;
import java.util.Objects;

public enum SqlDialect {
  POSTGRES("postgres"),
  CLICKHOUSE("clickhouse"),
  GAUSSDB("gaussdb"),
  DORIS("doris"),
  STARROCKS("starrocks"),
  TEST_DIALECT("test_dialect"), // only for test
  ;

  public final String name;

  public static SqlDialect fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(SqlDialect.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }

  SqlDialect(String name) {
    this.name = name;
  }
}