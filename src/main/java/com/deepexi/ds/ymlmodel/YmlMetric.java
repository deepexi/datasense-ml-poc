package com.deepexi.ds.ymlmodel;

import com.deepexi.ds.ComponentType;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlMetric {

  private final String version = "v1";
  private final String resource = ComponentType.METRICS_DEF.name;
  private final String name;
  private final String modelName;
  private final ImmutableList<String> dimensions; // 支持的 dimension 列表
  private final String aggregate;
  private final String dataType;

  public YmlMetric(String name,
      String modelName,
      List<String> dimensions,
      String aggregate,
      String dataType) {
    this.name = name;
    this.modelName = modelName;
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.aggregate = aggregate;
    this.dataType = dataType;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
