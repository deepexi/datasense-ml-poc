package com.deepexi.ds.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ModelHasCycleException;
import com.deepexi.ds.ModelException.ModelHasManyRootException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.UdfCastExpression;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.factory.YmlModelParser;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class ModelBuilderTest {

  @Test
  public void testBuild() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/dim_date.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);
    assertNotNull(rootModel);

    // name
    assertEquals("date_dim", rootModel.getName().getValue());

    // source
    Relation source = rootModel.getSource();
    assertTrue(source instanceof TableSource);
    TableSource ts = (TableSource) source;
    assertEquals("date_dim", ts.getTableName().getValue());

    // column
    List<Column> columns = rootModel.getColumns();
    assertEquals(5, columns.size());
    Map<String, Column> columnLookup = columns.stream()
        .collect(Collectors.toMap(Column::getAlias, Function.identity()));
    Column colA = columnLookup.get("d_date_sk");
    assertEquals(ColumnDataType.STRING, colA.getDataType());
    assertEquals("date_dim", ((Identifier) colA.getExpr()).getPrefix());
    assertEquals("d_date_sk", ((Identifier) colA.getExpr()).getValue());

    Column colB = columnLookup.get("d_date_id");
    assertEquals(ColumnDataType.INTEGER, colB.getDataType());
    assertEquals("date_dim", ((Identifier) colB.getExpr()).getPrefix());
    assertEquals("d_date_id", ((Identifier) colB.getExpr()).getValue());

    // dimensions
    List<Column> dims = rootModel.getDimensions();
    assertEquals(5, dims.size());
    Map<String, Column> dimLookup = dims.stream()
        .collect(Collectors.toMap(Column::getAlias, Function.identity()));
    Column dimA = dimLookup.get("d_date_sk");
    assertTrue(dimA.getExpr() instanceof Identifier);
    assertEquals("date_dim", ((Identifier) dimA.getExpr()).getPrefix());
    assertEquals("d_date_sk", ((Identifier) dimA.getExpr()).getValue());

    Column dimB = dimLookup.get("d_year");
    assertTrue(dimB.getExpr() instanceof Identifier);
    assertEquals("date_dim", ((Identifier) dimB.getExpr()).getPrefix());
    assertEquals("d_year", ((Identifier) dimB.getExpr()).getValue());
  }

  @Test
  void testBuild_illegal_table() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/dim_date_illegal.yml");
    assertThrows(ModelException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  void testBuild_join() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/join_2_models.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);
    assertNotNull(rootModel);
  }

  @Test
  void testBuild_join_illegal() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/join_2_models_illegal.yml");
    assertEquals(3, ymlModels.size());
    assertThrows(ModelException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_two_model_join_fail() {
    // miss store
    // YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel root = YmlModelParser.loadOneModel("debug/01_model_join_model.yml");
    List<YmlModel> ymlModels = Arrays.asList(storeSales, root);

    assertThrows(ModelNotFoundException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_stack_join_illegal() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/04_pile_join_illegal.yml");
    assertThrows(ModelException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_cycle_illegal() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/05_cycle_illegal.yml");
    assertThrows(ModelHasCycleException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_2_tree_illegal() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/06_two_trees.yml");
    assertThrows(ModelHasManyRootException.class, () -> ModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_cast() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/12_cast.yml");
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);
    assertNotNull(rootModel);
    assertEquals(6, rootModel.getColumns().size());

    // 隐式cast
    Column col0 = rootModel.getColumns().get(0);
    assertTrue(col0.getExpr() instanceof UdfCastExpression);
    UdfCastExpression cast0 = (UdfCastExpression) col0.getExpr();
    assertEquals(ColumnDataType.STRING, cast0.getToType());

    // 显式 转换: int->string
    Column col1 = rootModel.getColumns().get(1);
    assertEquals(ColumnDataType.STRING, col1.getDataType()); // 生成
    assertTrue(col1.getExpr() instanceof UdfCastExpression);
    UdfCastExpression cast1 = (UdfCastExpression) col1.getExpr();
    assertEquals(ColumnDataType.STRING, cast1.getToType());

    // 显式 转换: date->string
    Column col2 = rootModel.getColumns().get(2);
    assertEquals(ColumnDataType.STRING, col2.getDataType()); // 生成
    assertTrue(col2.getExpr() instanceof UdfCastExpression);
    UdfCastExpression cast2 = (UdfCastExpression) col2.getExpr();
    assertEquals(ColumnDataType.STRING, cast2.getToType());
    String pattern2 = ((StringLiteral) (cast2.getCastArgs().get(0))).getValue();
    assertEquals("'%Y-%m-%d %H:%M:%S'", pattern2);

    // 显式 转换: string->date
    Column col3 = rootModel.getColumns().get(3);
    assertEquals(ColumnDataType.DATE, col3.getDataType()); // 生成
    assertTrue(col3.getExpr() instanceof UdfCastExpression);
    UdfCastExpression cast3 = (UdfCastExpression) col3.getExpr();
    assertEquals(ColumnDataType.DATE, cast3.getToType());
    String pattern3 = ((StringLiteral) (cast3.getCastArgs().get(0))).getValue();
    assertEquals("'%Y/%m/%d %H'", pattern3);
  }
}
