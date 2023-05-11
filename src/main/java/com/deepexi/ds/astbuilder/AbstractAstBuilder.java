package com.deepexi.ds.astbuilder;

import com.deepexi.ds.SqlDialect;
import com.deepexi.ds.ast.Metric;
import com.deepexi.ds.ast.MetricQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;

public abstract class AbstractAstBuilder {

  protected final YmlMetricQuery ymlMetricQuery;
  protected final SqlDialect sqlDialect;

  protected AbstractAstBuilder(YmlMetricQuery ymlMetricQuery, SqlDialect sqlDialect) {
    this.ymlMetricQuery = ymlMetricQuery;
    this.sqlDialect = sqlDialect;
  }

  public abstract Model buildSingleModel();

  public abstract Metric buildMetric();

  public abstract MetricQuery buildMetricQuery();
}
