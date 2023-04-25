package com.deepexi.ds.ymlmodel.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.deepexi.ds.ymlmodel.YmlMetric;
import java.util.List;
import org.junit.jupiter.api.Test;

public class YmlMetricParserTest {

  @Test
  void testParseMetricFromResFile() {
    List<YmlMetric> ym = YmlMetricParser.loadModels("debug/08_metric.yml");
    assertNotNull(ym);
    assertEquals(2, ym.size());

    YmlMetric m1 = ym.stream().filter(x -> x.getName().equals("m1")).findAny().orElse(null);
    YmlMetric m2 = ym.stream().filter(x -> x.getName().equals("m2")).findAny().orElse(null);
    assertNotNull(m1);
    assertNotNull(m2);

    // m1
    assertEquals("join2", m1.getModelName());
    assertEquals("sum(*)", m1.getAggregate());
    assertEquals(0, m1.getDimensions().size());
    // m2
    assertEquals("foobar", m2.getModelName());
    assertEquals("avg(profit)", m2.getAggregate());
    assertEquals(2, m2.getDimensions().size());
  }

}
