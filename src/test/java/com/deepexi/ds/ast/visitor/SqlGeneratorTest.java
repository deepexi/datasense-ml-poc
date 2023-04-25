package com.deepexi.ds.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.expression.IdentifierPolicy.IdentifierPolicyBackTick;
import com.deepexi.ds.ast.expression.IdentifierPolicy.IdentifierPolicyNoQuote;
import com.deepexi.ds.ast.visitor.generator.SqlGenerator;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorContext;
import com.deepexi.ds.ast.visitor.generator.SqlGeneratorPgContext;
import com.deepexi.ds.builder.AstModelBuilder;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.factory.YmlModelParser;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SqlGeneratorTest {

  @Test
  void testVisitModel() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/01_base_table/reason.yml");
    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);
    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorContext(rootModel, SqlDialect.POSTGRES,
        IdentifierPolicyNoQuote.INSTANCE);
    String sql = generator.visitModel(context.getRoot(), context);
    assertNotNull(sql);
    System.out.println(sql);
  }

  @Test
  void testVisitModel_quote() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/01_base_table/reason.yml");
    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);

    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorContext(rootModel,
        SqlDialect.POSTGRES,
        IdentifierPolicyBackTick.INSTANCE);
    String sql = generator.visitModel(context.getRoot(), context);
    System.out.println(sql);
    assertTrue(sql.indexOf("`") > 0);
  }

  @Test
  public void testVisitModel_two_model_join() {
    // store_sales join store
    YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel join = YmlModelParser.loadOneModel("debug/01_2_model_join.yml");
    List<YmlModel> ymlModels = Arrays.asList(store, storeSales, join);

    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);

    // 准备 visit
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.visitModel(context.getRoot(), context);
    assertNotNull(sql);
    System.out.println(sql);
    assertNotNull(sql);
  }


  @Test
  public void testVisitMode_three_model_join() {
    // store_sales join store join item

    YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel item = YmlModelParser.loadOneModel("tpcds/01_base_table/item.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel root = YmlModelParser.loadOneModel("debug/02_3_model_join.yml");
    List<YmlModel> ymlModels = Arrays.asList(store, storeSales, item, root);

    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.visitModel(context.getRoot(), context);
    assertNotNull(sql);
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitModel_stack_join() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/03_pile_join.yml");
    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.visitModel(context.getRoot(), context);
    assertNotNull(sql);
    System.out.println(sql);
    assertNotNull(sql);
  }

  @Test
  public void testVisitModel_derived_column() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/07_derived_column.yml");
    Model rootModel = AstModelBuilder.singleTreeModel(ymlModels);

    // generate sql
    SqlGenerator generator = new SqlGenerator();
    SqlGeneratorContext context = new SqlGeneratorPgContext(rootModel);
    String sql = generator.visitModel(context.getRoot(), context);
    assertNotNull(sql);
    System.out.println(sql);
    assertNotNull(sql);
  }
}
