package com.deepexi.ds.ymlmodel;

import java.util.Objects;

public abstract class YmlSource {

  protected final String sourceType;

  public abstract String getAlias();

  protected YmlSource(String type) {
    this.sourceType = type;
  }

  public String getSourceType() {
    return sourceType;
  }

  public boolean matchSourceType(String sourceType) {
    return Objects.equals(this.sourceType, sourceType);
  }

  @Override
  public String toString() {
    return getAlias();
  }
}
