package com.deepexi.ds.ymlmodel;

import lombok.Getter;

@Getter
public class YmlWindowBoundary {

  private final String base;
  private final int offset;

  public YmlWindowBoundary(String base, int offset) {
    this.base = base;
    this.offset = offset;
  }
}