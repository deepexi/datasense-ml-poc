package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlFullQuery {

  private final YmlMetricQuery query;
  private final ImmutableList<YmlMetric> metrics;
  private final ImmutableList<YmlModel> models;
  private YmlDebug ymlDebug;

  public YmlFullQuery(YmlMetricQuery query, List<YmlMetric> metrics,
      List<YmlModel> models) {
    this.query = query;
    this.metrics = ImmutableList.copyOf(metrics);
    this.models = ImmutableList.copyOf(models);
  }

  public YmlFullQuery(YmlMetricQuery query, List<YmlMetric> metrics,
      List<YmlModel> models, YmlDebug ymlDebug) {
    this.query = query;
    this.metrics = ImmutableList.copyOf(metrics);
    this.models = ImmutableList.copyOf(models);
    this.ymlDebug = ymlDebug;
  }
}