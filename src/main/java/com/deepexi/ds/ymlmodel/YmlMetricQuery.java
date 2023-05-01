package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlMetricQuery {

  @Getter
  public static class YmlOrderBy {

    private final String name;
    private final String direction;

    public YmlOrderBy(String name, String direction) {
      this.name = name;
      if (direction == null) {
        direction = "asc";
      }
      this.direction = direction;
    }
  }

  private final String version = "v1";
  private final String resource = "metrics_query";

  private final String name;
  private final ImmutableList<String> metricNames;
  private final ImmutableList<String> dimensions;
  private final ImmutableList<String> modelFilters;
  private final ImmutableList<String> metricFilters;
  private final ImmutableList<YmlOrderBy> orderBys;
  private final Integer limit;
  private final Integer offset;
  private final YmlWindow window;

  public YmlMetricQuery(String name,
      List<String> metricNames,
      List<String> dimensions,
      List<String> modelFilters,
      List<String> metricFilters,
      List<YmlOrderBy> orderBys,
      Integer limit,
      Integer offset, YmlWindow window) {
    this.name = name;
    this.metricNames = ImmutableList.copyOf(metricNames);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.modelFilters = ImmutableList.copyOf(modelFilters);
    this.metricFilters = ImmutableList.copyOf(metricFilters);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.limit = limit;
    this.offset = offset;
    this.window = window;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
