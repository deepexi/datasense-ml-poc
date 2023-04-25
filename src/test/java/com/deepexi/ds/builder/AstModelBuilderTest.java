package com.deepexi.ds.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ModelHasManyRootException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Dimension;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.source.Source;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.yml2pojo.YmlModel;
import com.deepexi.ds.yml2pojo.YmlModelParser;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class AstModelBuilderTest {

  @Test
  public void testBuild() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/dim_date.yml");
    AstModelBuilder builder = AstModelBuilder.singleTreeModel(ymlModels);
    Model dimDate = builder.build();
    assertNotNull(dimDate);

    // name
    assertEquals("date_dim", dimDate.getName().getValue());

    // source
    Source source = dimDate.getSource();
    assertTrue(source instanceof TableSource);
    TableSource ts = (TableSource) source;
    assertEquals("date_dim", ts.getTableName().getValue());

    // column
    List<Column> columns = dimDate.getColumns();
    assertEquals(5, columns.size());
    Map<String, Column> columnLookup = columns.stream()
        .collect(Collectors.toMap(Column::getAlias, Function.identity()));
    Column colA = columnLookup.get("d_date_sk");
    assertEquals(ColumnDataType.STRING, colA.getType());
    assertEquals("date_dim", colA.getExpr().getPrefix());
    assertEquals("d_date_sk", colA.getExpr().getValue());

    Column colB = columnLookup.get("d_date_id");
    assertEquals(ColumnDataType.INTEGER, colB.getType());
    assertEquals("date_dim", colB.getExpr().getPrefix());
    assertEquals("d_date_id", colB.getExpr().getValue());

    // dimensions
    List<Dimension> dims = dimDate.getDimensions();
    assertEquals(5, dims.size());
    Map<String, Dimension> dimLookup = dims.stream()
        .collect(Collectors.toMap(Dimension::getName, Function.identity()));
    Dimension dimA = dimLookup.get("d_date_sk");
    assertEquals("d_date_sk", dimA.getRawExpr());

    Dimension dimB = dimLookup.get("d_year");
    assertEquals("d_year", dimB.getRawExpr());
  }

  @Test
  void testBuild_illegal_table() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/dim_date_illegal.yml");
    AstModelBuilder builder = AstModelBuilder.singleTreeModel(ymlModels);
    assertThrows(ModelException.class, () -> builder.build());
  }

  @Test
  void testBuild_join() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/join_2_models.yml");
    AstModelBuilder builder = AstModelBuilder.singleTreeModel(ymlModels);
    Model rootModel = builder.build();
    assertNotNull(rootModel);
  }

  @Test
  void testBuild_join_illegal() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/join_2_models_illegal.yml");
    assertEquals(3, ymlModels.size());
    AstModelBuilder builder = AstModelBuilder.singleTreeModel(ymlModels);
    assertThrows(ModelNotFoundException.class, () -> builder.build());
  }

  @Test
  public void testBuild_two_model_join_fail() {
    // miss store
    // YmlModel store = YmlModelParser.loadOneModel("tpcds/01_base_table/store.yml");
    YmlModel storeSales = YmlModelParser.loadOneModel("tpcds/01_base_table/store_sales.yml");
    YmlModel root = YmlModelParser.loadOneModel("tpcds/02_join/01_2_model_join.yml");
    List<YmlModel> ymlModels = Arrays.asList(/*store,*/ storeSales, root);

    assertThrows(ModelNotFoundException.class, () -> AstModelBuilder.singleTreeModel(ymlModels));
  }

  @Test
  public void testBuild_stack_join_illegal() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/02_join/04_pile_join_illegal.yml");
    AstModelBuilder builder = AstModelBuilder.singleTreeModel(ymlModels);
    assertThrows(ModelNotFoundException.class, () -> builder.build());
  }


  @Test
  public void testBuild_cycle_illegal() {
    // join1 = store_sales join store
    // join2 = join1 join item
    List<YmlModel> ymlModels = YmlModelParser.loadModels("tpcds/02_join/06_two_trees.yml");
    assertThrows(ModelHasManyRootException.class, () -> AstModelBuilder.singleTreeModel(ymlModels));
  }
}
