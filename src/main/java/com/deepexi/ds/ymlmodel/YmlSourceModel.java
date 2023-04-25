package com.deepexi.ds.ymlmodel;


import lombok.Getter;

@Getter
public class YmlSourceModel extends YmlSource {

  private static final String type = "model_ml";
  private final String modelName;

  @Override
  public String getAlias() {
    return modelName;
  }

  public YmlSourceModel(String modelName) {
    super(type);
    this.modelName = modelName;
  }
}
