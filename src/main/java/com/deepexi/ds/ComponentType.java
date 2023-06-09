package com.deepexi.ds;

public enum ComponentType {
  MODEL_DEF("model_def"),
  METRICS_DEF("metric_def"),
  METRICS_QUERY("metrics_query"),
  MODEL_DEBUG("model_debug"),
  ;

  public final String name;

  ComponentType(String name) {
    this.name = name;
  }
}