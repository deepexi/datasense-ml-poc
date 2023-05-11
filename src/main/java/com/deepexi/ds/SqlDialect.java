package com.deepexi.ds;

import java.util.Arrays;
import java.util.Objects;

public enum SqlDialect {
  POSTGRES("postgres", true),
  CLICKHOUSE("clickhouse", true),
  GAUSSDB("gaussdb", true),
  DORIS("doris", false),
  STARROCKS("starrocks", false),
  TEST_DIALECT("test_dialect", false), // only for test
  ;

  public final String name;
  public final boolean supportWindowRange;

  public static SqlDialect fromName(String name) {
    if (name == null) {
      return null;
    }
    String lowerName = name.toLowerCase();
    return Arrays.stream(SqlDialect.values())
        .filter(x -> Objects.equals(x.name, lowerName))
        .findAny().orElse(null);
  }

  SqlDialect(String name, boolean supportWindowRange) {
    this.name = name;
    this.supportWindowRange = supportWindowRange;
  }
}