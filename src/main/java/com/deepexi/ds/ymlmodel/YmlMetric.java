package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlMetric {

  private final String version = "v1";
  private final String resource = "metrics_ml";
  private final String name;
  private final String modelName;
  private final ImmutableList<String> dimensions;
  private final String aggregate;

  public YmlMetric(String name,
      String modelName,
      List<String> dimensions,
      String aggregate) {
    this.name = name;
    this.modelName = modelName;
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.aggregate = aggregate;
  }
}
