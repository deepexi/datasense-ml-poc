package com.deepexi.ds.ymlmodel;


import lombok.Getter;

@Getter
public class YmlDimension {

  private final String name;

  public YmlDimension(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
