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
    assertEquals("d_date_sk", dimA.getRawExpr());

    Column dimB = dimLookup.get("d_year");
    assertEquals("d_year", dimB.getRawExpr());
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
    List<YmlModel> ymlModels = Arrays.asList(/*store,*/ storeSales, root);

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
}
