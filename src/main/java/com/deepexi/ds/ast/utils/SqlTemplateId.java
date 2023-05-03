package com.deepexi.ds.ast.utils;

public enum SqlTemplateId {

  model_001("model_001"),
  metric_bind_query_001("metric_bind_query_001"),
  case_when_001("case_when_001"),
  ;

  public final String fileName;

  SqlTemplateId(String fileName) {
    this.fileName = fileName;
  }
}
