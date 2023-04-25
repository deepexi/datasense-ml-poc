package com.deepexi.ds.ast;

public enum ComponentType {
  MODEL_ML("model_def"),
  METRICS_ML("metrics_def"),
  METRICS_QL("metrics_query");

  public final String name;

  ComponentType(String name) {
    this.name = name;
  }
}