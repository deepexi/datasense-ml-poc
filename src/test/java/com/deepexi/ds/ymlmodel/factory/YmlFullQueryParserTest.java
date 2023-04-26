package com.deepexi.ds.ymlmodel.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

public class YmlFullQueryParserTest {

  @Test
  void testParseMetricFromResFile() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/10_full.yml");
    assertNotNull(ctx);

    // query
    YmlMetricQuery query = ctx.getQuery();
    assertNotNull(query);
    assertEquals("metric_1_query", query.getName());
    assertNotNull(query.getMetricNames());
    assertEquals(1, query.getMetricNames().size());
    assertEquals("metric_1", query.getMetricNames().get(0));
    assertEquals(2, query.getDimensions().size());
    assertEquals("s_store_name", query.getDimensions().get(0));
    assertEquals("i_product_name", query.getDimensions().get(1));
    assertEquals(2, query.getModelFilters().size());
    assertEquals("a=1", query.getModelFilters().get(0));
    assertEquals("b=2", query.getModelFilters().get(1));
    assertEquals(2, query.getMetricFilters().size());
    assertEquals("c=3", query.getMetricFilters().get(0));
    assertEquals("d=4", query.getMetricFilters().get(1));

    // metrics
    ImmutableList<YmlMetric> metrics = ctx.getMetrics();
    assertNotNull(metrics);
    assertEquals(1, metrics.size());

    // models
    assertNotNull(ctx.getModels());
    assertEquals(5, ctx.getModels().size());
  }
}
