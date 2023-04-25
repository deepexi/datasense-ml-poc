package com.deepexi.ds.yml2pojo;


import lombok.Getter;

@Getter
public class YmlColumn {

  private final String name;
  private final String expr;
  private final String type;

  public YmlColumn(String name, String expr, String type) {
    this.name = name;
    this.expr = expr;
    this.type = type;
  }
}
