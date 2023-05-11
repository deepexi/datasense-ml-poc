package com.deepexi.ds.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.JdbcQueryAndGetAllResult;
import com.deepexi.ds.JdbcUtils;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.visitor.generator.SqlGenerator;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorContext;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorPgContext;
import com.deepexi.ds.builder.AstBuilder;
import com.deepexi.ds.ymlmodel.YmlDebug;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.factory.YmlFullQueryParser;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

/**
 * <pre>
 * 将生成的sql 与手写的sql进行比较, 检测有效性
 * 在这里进行 测试的 yml, 必须有 YmlDebug节点, 手写sql, 比对结果
 * </pre>
 */
public class SqlValidateTest {

  @Test
  public void testVisitMetricBindQuery_case04() {
    rows_data_equal("tpcds/02_biz/case04_e2e.yml");
  }

  @Test
  public void testVisitMetricBindQuery_case05() {
    count_equal("tpcds/02_biz/case05_e2e.yml");
  }

  @Test
  public void testVisitMetricBindQuery_case06() {
    rows_data_equal("tpcds/02_biz/case06_order_by_e2e.yml");
  }


  @Test
  public void testVisitMetricBindQuery_case07() {
    rows_data_equal("tpcds/02_biz/case07_window_unbounded_e2e.yml");
  }

  @Test
  public void testVisitMetricBindQuery_case08() {
    rows_data_equal("tpcds/02_biz/case08_window_3_day_e2e.yml");
  }

  @Test
  public void testVisitMetricBindQuery_case09() {
    rows_data_equal("tpcds/02_biz/case09_window_3_day_e2e.yml");
  }

  private void count_equal(String resOfYml) {
    // parse yml
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(resOfYml);
    YmlDebug ymlDebug = ctx.getYmlDebug();
    assertNotNull(ymlDebug, "e2e测试, 必须有YmlDebug节点");
    String manualSql = ymlDebug.getSql();

    // generate sql
    AstNode node = new AstBuilder(ctx).build();
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String autoSql = generator.process(context.getRoot(), context);

    // check rows count
    int count1 = JdbcUtils.queryCount(manualSql);
    int count2 = JdbcUtils.queryCount(autoSql);
    System.out.println("count1=" + count1 + ", count2=" + count2);
    assertEquals(count1, count2, "fail " + resOfYml);
  }

  private void rows_data_equal(String resOfYml) {
    // parse yml
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(resOfYml);
    YmlDebug ymlDebug = ctx.getYmlDebug();
    assertNotNull(ymlDebug, "e2e测试, 必须有YmlDebug节点");
    String manualSql = ymlDebug.getSql();

    // generate sql
    AstNode node = new AstBuilder(ctx).build();
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String autoSql = generator.process(context.getRoot(), context);

    // check rows data
    List<List<Object>> r1 = JdbcQueryAndGetAllResult.querySelect(manualSql);
    List<List<Object>> r2 = JdbcQueryAndGetAllResult.querySelect(autoSql);
    assertEquals(r1.size(), r2.size());
    for (int i = 0; i < r1.size(); i++) {
      assertTrue(tupleEquals(r1.get(i), r2.get(i)), String.format("row[%s] not equal", i));
    }
  }

  private boolean tupleEquals(List<Object> o1, List<Object> o2) {
    if (o1 == null || o2 == null) {
      return false;
    }
    if (o1.size() != o2.size()) {
      return false;
    }
    int sz = o1.size();
    for (int i = 0; i < sz; i++) {
      Object e1 = o1.get(i);
      Object e2 = o2.get(i);
      if (!Objects.equals(e1, e2)) {
        return false;
      }
    }
    return true;
  }
}
