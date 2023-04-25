package com.deepexi.ds.yml2pojo;


import lombok.Getter;

@Getter
public class YmlColumn {

  private final String name;
  private final String expr;
  private final String dataType;
  private final String hint;

  public YmlColumn(String name, String expr, String dataType) {
    this.name = name;
    this.expr = expr;
    this.dataType = dataType;
    this.hint = "basic";
  }

  public YmlColumn(String name, String expr, String dataType, String hint) {
    this.name = name;
    this.expr = expr;
    this.dataType = dataType;
    this.hint = hint;
  }
}
