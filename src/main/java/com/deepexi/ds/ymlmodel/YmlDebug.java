package com.deepexi.ds.ymlmodel;

import lombok.Getter;

/**
 * <pre>
 * version: v1
 * resource: model_debug
 * sql: >
 *   select *
 *   from table
 *   where a > 0
 * </pre>
 * 这个组件用于测试
 */
@Getter
public class YmlDebug {

  private final String sql;

  public YmlDebug(String sql) {
    this.sql = sql;
  }
}
