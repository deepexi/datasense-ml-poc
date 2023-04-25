package com.deepexi.ds.yaml2pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepexi.ds.yml2pojo.YmlColumn;
import com.deepexi.ds.yml2pojo.YmlDimension;
import com.deepexi.ds.yml2pojo.YmlModel;
import com.deepexi.ds.yml2pojo.YmlSource;
import com.deepexi.ds.yml2pojo.YmlSourceTable;
import com.deepexi.ds.yml2pojo.YmlModelParser;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class YmlModelParserTest {

  @Test
  void testParseModelFromResFile_one_model() {
    YmlModel dimDate = YmlModelParser.loadOneModel("debug/dim_date.yml");
    assertNotNull(dimDate);

    // name
    assertEquals("date_dim", dimDate.getName());

    // source
    YmlSource source = dimDate.getSource();
    assertTrue(source instanceof YmlSourceTable);
    assertEquals("date_dim", ((YmlSourceTable) source).getTableName());

    // column
    List<YmlColumn> columns = dimDate.getColumns();
    assertEquals(5, columns.size());
    Map<String, YmlColumn> columnLookup = columns.stream()
        .collect(Collectors.toMap(YmlColumn::getName, Function.identity()));
    YmlColumn colA = columnLookup.get("d_date_sk");
    assertEquals("string", colA.getType());
    assertEquals("d_date_sk", colA.getExpr());

    YmlColumn colB = columnLookup.get("d_date_id");
    assertEquals("int", colB.getType());
    assertEquals("date_dim.d_date_id", colB.getExpr());

    // dimensions
    List<YmlDimension> dims = dimDate.getDimensions();
    assertEquals(5, dims.size());
    Map<String, YmlDimension> dimLookup = dims.stream()
        .collect(Collectors.toMap(YmlDimension::getName, Function.identity()));
    YmlDimension dimA = dimLookup.get("d_date_sk");
    assertNull(dimA.getType());
    assertEquals("d_date_sk", dimA.getExpr());

    YmlDimension dimB = dimLookup.get("d_year");
    assertNull(dimB.getType());
    assertEquals("d_year", dimB.getExpr());
  }

  @Test
  void testParseModelFromResFile_two_model() {
    List<YmlModel> mlList = YmlModelParser.loadModels("debug/store_and_item.yml");
    assertNotNull(mlList);
    assertEquals(mlList.size(), 2);
    assertEquals("model_store", mlList.get(0).getName());
    assertEquals("model_item", mlList.get(1).getName());
  }
}
