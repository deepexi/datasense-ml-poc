package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlModel {

  private final String version = "v1";
  private final String resource = "model_ml";
  private final String name;
  private final YmlSource source;
  private final ImmutableList<YmlJoin> joins;
  private final ImmutableList<YmlColumn> columns;
  private final ImmutableList<YmlDimension> dimensions;

  public YmlModel(
      String name,
      YmlSource source,
      List<YmlJoin> joins,
      List<YmlColumn> columns,
      List<YmlDimension> dimensions) {
    this.name = name;
    this.source = source;
    this.joins = ImmutableList.copyOf(joins);
    this.columns = ImmutableList.copyOf(columns);
    this.dimensions = ImmutableList.copyOf(dimensions);
  }
}
