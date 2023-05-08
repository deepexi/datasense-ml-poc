package com.deepexi.ds.ymlmodel.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlWindow;
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
    assertEquals("s_store_name=1", query.getModelFilters().get(0));
    assertEquals("s_store_name=2", query.getModelFilters().get(1));
    assertEquals(2, query.getMetricFilters().size());
    assertEquals("metric_1=3", query.getMetricFilters().get(0));
    assertEquals("metric_1=4", query.getMetricFilters().get(1));

    // metrics
    ImmutableList<YmlMetric> metrics = ctx.getMetrics();
    assertNotNull(metrics);
    assertEquals(1, metrics.size());

    // models
    assertNotNull(ctx.getModels());
    assertEquals(5, ctx.getModels().size());
  }

  @Test
  void testParseMetricFromResFile_orderby() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case06_order_by_e2e.yml");
    assertNotNull(ctx);
    assertNotNull(ctx.getQuery().getOrderBys());
    assertEquals(3, ctx.getQuery().getOrderBys().size());

    assertNotNull(ctx.getQuery().getLimit());
    assertEquals(100, ctx.getQuery().getLimit());

    assertNotNull(ctx.getQuery().getOffset());
    assertEquals(1, ctx.getQuery().getOffset());
  }


  @Test
  void testParseMetricFromResFile_window() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case07_window_unbounded_e2e.yml");
    assertNotNull(ctx);
    YmlWindow window = ctx.getQuery().getWindow();
    assertNotNull(window);
    // getPartitions
    assertEquals("unbounded   month", window.getTrailing());
  }
}
