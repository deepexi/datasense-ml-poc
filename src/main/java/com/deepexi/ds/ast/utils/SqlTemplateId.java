package com.deepexi.ds.ast.utils;

public enum SqlTemplateId {

  model_001("model_001"),
  model_001_cte("model_001_cte"),
  metric_bind_query_001("metric_bind_query_001"),
  case_when_001("case_when_001"),
  window_row_frame_001("window_row_frame_001"),
  udf_create_date_by_ymd("udf_create_date_by_ymd"),
  udf_date_diff("udf_date_diff"),
  // === udf cast ===
  udf_cast_to_bool("udf_cast_to_bool"),
  udf_cast_to_date("udf_cast_to_date"),
  udf_cast_to_datetime("udf_cast_to_datetime"),
  udf_cast_to_integer("udf_cast_to_integer"),
  udf_cast_to_decimal("udf_cast_to_decimal"),
  udf_cast_to_string("udf_cast_to_string"),
  udf_cast_to_time("udf_cast_to_time"),
  udf_cast_to_timestamp("udf_cast_to_timestamp"),
  ;

  public final String fileName;

  SqlTemplateId(String fileName) {
    this.fileName = fileName;
  }
}
