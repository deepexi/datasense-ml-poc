package com.deepexi.ds.ymlmodel.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class YmlMetricMetricQueryParserTest {

  @Test
  void testParseMetricQueryFromResFile() {
    YmlMetricQuery query = YmlMetricQueryParser.loadOneModel("debug/09_metrics_query.yml");
    assertNotNull(query);

    // name
    assertEquals("store_sales_count", query.getName());
    // metric_names

    assertIterableEquals(
        Arrays.asList("store_sales_count", "store_sales_count"),
        query.getMetricNames());

    assertIterableEquals(
        Arrays.asList("s_store_name", "i_product_name"),
        query.getDimensions());

    assertIterableEquals(
        Arrays.asList("quantity > 0", "status='paid'"),
        query.getModelFilters());

    assertIterableEquals(
        Arrays.asList("d_year >= 1990", "d_year <= 1999"),
        query.getMetricFilters());

  }

}
