package com.deepexi.ds.builder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.factory.YmlFullQueryParser;
import org.junit.jupiter.api.Test;

public class FullAstBuilderTest {

  @Test
  public void testBuild() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/10_full.yml");
    MetricBindQuery fullAst = new FullAstBuilder(ctx).build();
    assertNotNull(fullAst);
  }

  @Test
  public void testBuild_illegal_1_dim() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/11_full_illegal_1.yml");
    assertThrows(ModelException.class, () -> new FullAstBuilder(ctx).build());
  }
}
