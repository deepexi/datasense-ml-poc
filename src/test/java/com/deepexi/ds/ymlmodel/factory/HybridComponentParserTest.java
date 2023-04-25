package com.deepexi.ds.ymlmodel.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.deepexi.ds.ymlmodel.factory.HybridComponentParser.SelfContainedContext;
import org.junit.jupiter.api.Test;

public class HybridComponentParserTest {

  @Test
  void testParseMetricFromResFile() {
    SelfContainedContext ctx = HybridComponentParser.loadFromRes("debug/10_combine.yml");
    assertNotNull(ctx);
  }
}
