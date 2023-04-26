package com.deepexi.ds.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.visitor.generator.SqlGenerator;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorContext;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorPgContext;
import com.deepexi.ds.builder.MetricBindQueryBuilder;
import com.deepexi.ds.ymlmodel.YmlDebug;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.factory.YmlFullQueryParser;
import org.junit.jupiter.api.Test;

/**
 * <pre>
 * 将生成的sql 与手写的sql进行比较, 检测有消息
 * 在这里进行 测试的 yml, 必须有 YmlDebug节点, 手写sql, 比对结果
 * </pre>
 */
public class SqlValidateTest {

  @Test
  public void testVisitMetricBindQuery_case04() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case04.yml");
    YmlDebug ymlDebug = ctx.getYmlDebug();
    assertNotNull(ymlDebug, "这里测试的yml, 必须有YmlDebug节点");
    String manualSql = ymlDebug.getSql();

    // generate sql
    AstNode node = new MetricBindQueryBuilder(ctx).build();
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String autoSql = generator.process(context.getRoot(), context);

    int count1 = JdbcUtils.queryCount(manualSql);
    int count2 = JdbcUtils.queryCount(autoSql);
    assertEquals(count1, count2);
  }
}
