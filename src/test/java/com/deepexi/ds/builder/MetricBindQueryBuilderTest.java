package com.deepexi.ds.builder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.factory.YmlFullQueryParser;
import org.junit.jupiter.api.Test;

public class MetricBindQueryBuilderTest {

  @Test
  public void testBuild() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/10_full.yml");
    MetricBindQuery fullAst = (MetricBindQuery) new MetricBindQueryBuilder(ctx).build();
    assertNotNull(fullAst);
  }

  @Test
  public void testBuild_illegal_1_dim() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/11_full_illegal_1.yml");
    assertThrows(ModelException.class, () -> new MetricBindQueryBuilder(ctx).build());
  }

  @Test
  public void testBuild_window_unbounded() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case07_window_unbounded_e2e.yml");
    Relation fullAst = new MetricBindQueryBuilder(ctx).build();
    assertNotNull(fullAst);
  }

  @Test
  public void testBuild_window_range_3_day() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case08_window_3_day_e2e.yml");
    Relation fullAst = new MetricBindQueryBuilder(ctx).build();
    assertNotNull(fullAst);
  }

}
