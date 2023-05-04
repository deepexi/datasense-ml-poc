package com.deepexi.ds.ymlmodel;

import lombok.Getter;

@Getter
public class YmlFrameBoundary {

  private final String base;
  private final int offset;

  public YmlFrameBoundary(String base, int offset) {
    this.base = base;
    this.offset = offset;
  }
}