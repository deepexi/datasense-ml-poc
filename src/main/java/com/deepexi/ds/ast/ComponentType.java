package com.deepexi.ds.ast;

public enum ComponentType {
  MODEL_ML("model_ml"),
  METRICS_ML("metrics_ml"),
  METRICS_QL("metrics_ql");

  public final String name;

  ComponentType(String name) {
    this.name = name;
  }
}