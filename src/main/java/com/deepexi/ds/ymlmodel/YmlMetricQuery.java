package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

//# MetricQL
//    version: v1
//    resource: MetricQL
//    name: monthly_profit_of_each_store_between_1990_1999
//    metrics: [ metric_sum_profit ]
//    model_filter: [ "quanlity > 0", "net_profit is not null" ] # 是否带前缀, 如果是函数, 如 not_null, 怎么处理
//    metrics_filter: [ "d_year >= 1990", "d_year <= 1999"]      # 是否需要带model前缀?
//    # 对维度字段的过滤, 需要在结果集后进行, 对非维度字段的过滤, 必须在Model上进行
//    dimensions: [ store_id, d_year, d_moy ]

@Getter
public class YmlMetricQuery {

  private final String version = "v1";
  private final String resource = "metrics_ql";

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
}
