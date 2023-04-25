package com.deepexi.ds.yml2pojo;

import java.util.Objects;

public abstract class YmlSource {

  protected final String type;

  public abstract String getAlias();

  protected YmlSource(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public boolean matchType(String type) {
    return Objects.equals(this.type, type);
  }

}
