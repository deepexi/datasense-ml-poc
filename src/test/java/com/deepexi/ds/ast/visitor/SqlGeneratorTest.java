package com.deepexi.ds.ast.visitor;

import static com.deepexi.ds.ast.utils.ResUtils.noPlaceHolder;
import static com.deepexi.ds.ast.visitor.generator.IdentifierShowPolicy.NO_TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.DevConfig;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.visitor.generator.IdentifierQuotePolicy.IdentifierPolicyBackTick;
import com.deepexi.ds.ast.visitor.generator.IdentifierQuotePolicy.IdentifierPolicyNoQuote;
import com.deepexi.ds.ast.visitor.generator.IdentifierShowPolicy;
import com.deepexi.ds.ast.visitor.generator.SqlGenerator;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorContext;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorPgContext;
import com.deepexi.ds.builder.MetricBindQueryBuilder;
import com.deepexi.ds.builder.ModelBuilder;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.factory.YmlFullQueryParser;
import com.deepexi.ds.ymlmodel.factory.YmlModelParser;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SqlGeneratorTest {

  @Test
  void testVisitModel() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/01_base_table/reason.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);
    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorContext(
        rootModel,
        SqlDialect.POSTGRES,
        IdentifierPolicyNoQuote.NO_QUOTE,
        IdentifierShowPolicy.SHOW_TABLE_NAME);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);

    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    if (DevConfig.DEBUG) {
      assertTrue(sql.contains("res/sql/postgres")); // 这个是 sql模板中保留的
    }
    System.out.println(sql);
  }

  @Test
  void testVisitModel_quote() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/01_base_table/reason.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorContext(
        rootModel,
        SqlDialect.POSTGRES,
        IdentifierPolicyBackTick.BACK_TICK,
        IdentifierShowPolicy.SHOW_TABLE_NAME);
    String sql = generator.process(context.getRoot(), context);
    System.out.println(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    assertTrue(sql.indexOf("`") > 0);
  }

  @Test
  public void testVisitModel_two_model_join() {
    // store_sales join store
    YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel join = YmlModelParser.loadOneModel("debug/01_model_join_model.yml");
    List<YmlModel> ymlModels = Arrays.asList(store, storeSales, join);

    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }


  @Test
  public void testVisitMode_three_model_join() {
    // store_sales join store join item

    YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel item = YmlModelParser.loadOneModel("tpcds/01_base_table/item.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel root = YmlModelParser.loadOneModel("debug/02_model_join_2models.yml");
    List<YmlModel> ymlModels = Arrays.asList(store, storeSales, item, root);

    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitModel_stack_join() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/03_pile_join.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitModel_derived_column() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/07_derived_column.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("debug/10_full.yml");
    AstNode node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testColumnCast() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/12_cast.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }


  @Test
  public void testVisitMetricBindQuery_case01() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case01.yml");
    AstNode node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case02() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case02.yml");
    AstNode node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case03() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case03.yml");
    AstNode node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case04() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case04_e2e.yml");
    AstNode node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case05() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case05_e2e.yml");
    MetricBindQuery node = (MetricBindQuery) new MetricBindQueryBuilder(ctx).build();
    assertEquals(2, node.getMetrics().size());

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case06() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes("tpcds/02_biz/case06_order_by_e2e.yml");
    MetricBindQuery node = (MetricBindQuery) new MetricBindQueryBuilder(ctx).build();
    assertEquals(2, node.getMetrics().size());

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case07() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case07_window_unbounded_e2e.yml");
    Relation node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(node, NO_TABLE_NAME);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case08() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case08_window_3_day_e2e.yml");
    Relation node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    // SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    SqlGeneratorContext context = new SqlGeneratorPgContext(node, NO_TABLE_NAME);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitMetricBindQuery_case09() {
    YmlFullQuery ctx = YmlFullQueryParser.loadFromRes(
        "tpcds/02_biz/case09_window_3_day_e2e.yml");
    Relation node = new MetricBindQueryBuilder(ctx).build();

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    // SqlGeneratorContext context = new SqlGeneratorPgContext(node);
    SqlGeneratorContext context = new SqlGeneratorPgContext(node, NO_TABLE_NAME);
    String sql = generator.process(context.getRoot(), context);
    assertNotNull(sql);
    assertTrue(noPlaceHolder(sql)); // 所有占位符都已被替换
    System.out.println(sql);
    assertNotNull(sql);
  }
}
