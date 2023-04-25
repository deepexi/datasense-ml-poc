package com.deepexi.ds.ymlmodel;


import lombok.Getter;

@Getter
public class YmlDimension {

  private final String name;
  private final String expr;
  private final String type;

  public YmlDimension(String name, String expr, String type) {
    this.name = name;
    this.expr = expr;
    this.type = type;
  }
}
