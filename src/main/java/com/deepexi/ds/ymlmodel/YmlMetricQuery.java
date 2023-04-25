package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlMetricQuery {

  private final String version = "v1";
  private final String resource = "metrics_query";

  private final String name;
  private final ImmutableList<String> metricNames;
  private final ImmutableList<String> dimensions;
  private final ImmutableList<String> modelFilters;
  private final ImmutableList<String> dimFilters;

  public YmlMetricQuery(String name,
      List<String> metricNames,
      List<String> dimensions,
      List<String> modelFilters,
      List<String> dimFilters) {
    this.name = name;
    this.metricNames = ImmutableList.copyOf(metricNames);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.dimFilters = ImmutableList.copyOf(dimFilters);
  }

  @Override
  public String toString() {
    return this.name;
  }
}
