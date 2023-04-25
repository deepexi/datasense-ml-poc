package com.deepexi.ds.yml2pojo;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;


/**
 * TODO 等版本稍稳定, 将字符串常量全部提取出来
 */
@SuppressWarnings("unchecked")
public class YmlModelParser {

  public static final String resource_value_model = "model_ml";
  public static final String resource_key = "resource";
  public static final String source_key = "source";
  public static final String joins_key = "joins";
  public static final String columns_key = "columns";
  public static final String dimensions_key = "dimensions";

  // source
  public static final String source_key_type = "type";
  public static final String source_key_table = "table";
  public static final String source_key_datasource = "datasource";
  public static final String source_key_model_name = "model_name";

  public static final String source_value_type_table = "table";
  public static final String source_value_type_model = "model_ml";


  private YmlModelParser() {
  }

  public static YmlModel loadOneModel(String resFile) {
    Yaml yaml = new Yaml();
    InputStream inputStream = YmlModelParser.class.getClassLoader().getResourceAsStream(resFile);
    Map<String, Object> map = yaml.load(inputStream);
    return loadOneModel(map);
  }

  public static List<YmlModel> loadModels(String resFile) {
    Yaml yaml = new Yaml();

    try {
      InputStream input = YmlModelParser.class.getClassLoader().getResourceAsStream(resFile);
      Iterable<Object> objects = yaml.loadAll(input);
      List<YmlModel> mlList = new ArrayList<>();
      for (Object obj : objects) {
        Map<String, Object> item = (Map<String, Object>) obj;
        YmlModel one = loadOneModel(item);
        mlList.add(one);
      }
      return mlList;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private static YmlModel loadOneModel(Map<String, Object> map) {
    Object resource = map.get(resource_key);
    if (!(resource instanceof String)) {
      return null;
    }
    if (!Objects.equals(resource_value_model, resource)) {
      return null;
    }

    // name
    String name = getStringElseThrow(map, "name");

    // source
    Map<String, Object> source1 = (Map<String, Object>) (map.get(source_key));
    Objects.requireNonNull(source1);
    YmlSource source = parseSource(source1);

    // joins
    List<Map<String, Object>> joins1 = (List<Map<String, Object>>) (map.get(joins_key));
    List<YmlJoin> joins = parseJoins(joins1);

    // columns
    List<Map<String, Object>> columns1 = (List<Map<String, Object>>) (map.get(columns_key));
    List<YmlColumn> columns = parseColumn(columns1);

    // dimensions
    List<Map<String, Object>> dim1 = (List<Map<String, Object>>) (map.get(dimensions_key));
    List<YmlDimension> dim = parseDimension(dim1);

    return new YmlModel(name, source, joins, columns, dim);
  }

  private static YmlSource parseSource(Map<String, Object> map) {
    String type = getStringElseThrow(map, source_key_type);

    if (Objects.equals(source_value_type_table, type)) {
      String table = getStringElseThrow(map, source_key_table);
      String datasource = getStringElseThrow(map, source_key_datasource);
      return new YmlSourceTable(datasource, table);
    }

    if (Objects.equals(source_value_type_model, type)) {
      String modelName = getStringElseThrow(map, source_key_model_name);
      return new YmlSourceModel(modelName);
    }

    throw new ModelException("unknown source type=" + type);
  }

  private static List<YmlJoin> parseJoins(List<Map<String, Object>> list) {
    if (list == null || list.isEmpty()) {
      return EMPTY_LIST;
    }

    List<YmlJoin> joins = new ArrayList<>(list.size());
    for (Map<String, Object> j : list) {
      String modelName = getStringElseThrow(j, "model_name");
      String joinType = getStringElseThrow(j, "join_type");
      List<String> conditions = (List<String>) (j.get("conditions"));
      YmlJoin oneJoin = new YmlJoin(modelName, joinType, conditions);
      joins.add(oneJoin);
    }
    return joins;
  }


  private static List<YmlColumn> parseColumn(List<Map<String, Object>> list) {
    if (list == null || list.isEmpty()) {
      return EMPTY_LIST;
    }
    List<YmlColumn> columns = new ArrayList<>(list.size());
    for (Map<String, Object> col : list) {
      String name = getStringElseThrow(col, "name");
      String expr = getStringElse(col, "expr", name);
      String type = getStringElse(col, "data_type", null);
      columns.add(new YmlColumn(name, expr, type));
    }
    return columns;
  }

  private static List<YmlDimension> parseDimension(List<Map<String, Object>> list) {
    if (list == null || list.isEmpty()) {
      return EMPTY_LIST;
    }
    List<YmlDimension> dims = new ArrayList<>(list.size());
    for (Map<String, Object> dim : list) {
      String name = getStringElseThrow(dim, "name");
      String expr = getStringElse(dim, "expr", name);
      String type = getStringElse(dim, "type", null);
      dims.add(new YmlDimension(name, expr, type));
    }
    return dims;
  }

  private static String getStringElseThrow(Map<String, Object> map, String key) {
    Object val = map.get(key);
    if (val == null) {
      throw new ModelException(String.format("key [%s] not found in map", key));
    }
    if (!(val instanceof String)) {
      throw new ModelException(String.format("value of %s is not String", key));
    }
    return (String) (val);
  }

  private static String getStringElse(Map<String, Object> map, String key, String defVal) {
    Object val = map.get(key);
    if (val == null) {
      return defVal;
    }
    if (!(val instanceof String)) {
      throw new ModelException(String.format("value of %s is not String", key));
    }
    return (String) (val);
  }
}
