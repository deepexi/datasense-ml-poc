package com.deepexi.ds.ymlmodel;

import lombok.Getter;

@Getter
public class YmlSourceTable extends YmlSource {

  private static final String type = "table";
  private final String dataSource;
  private final String tableName;

  public YmlSourceTable(String dataSource, String tableName) {
    super(type);
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  @Override
  public String getAlias() {
    return tableName;
  }
}