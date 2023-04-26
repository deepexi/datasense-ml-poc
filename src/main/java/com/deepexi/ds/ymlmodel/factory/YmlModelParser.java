package com.deepexi.ds.ymlmodel.factory;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.JoinType;
import com.deepexi.ds.ymlmodel.YmlColumn;
import com.deepexi.ds.ymlmodel.YmlDimension;
import com.deepexi.ds.ymlmodel.YmlJoin;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.YmlSource;
import com.deepexi.ds.ymlmodel.YmlSourceModel;
import com.deepexi.ds.ymlmodel.YmlSourceTable;
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

  public static final String resource_key = "resource";
  public static final String source_key = "source";
  public static final String joins_key = "joins";
  public static final String columns_key = "columns";
  public static final String dimensions_key = "dimensions";

  // source
  public static final String source_key_type = "source_type";
  public static final String source_key_table = "table";
  public static final String source_key_datasource = "datasource";
  public static final String source_key_model_name = "model_name";
  public static final String source_value_type_table = "table";
  public static final String source_value_type_model = "model_def";


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

    InputStream input = YmlModelParser.class.getClassLoader().getResourceAsStream(resFile);
    Iterable<Object> objects = yaml.loadAll(input);
    List<YmlModel> mlList = new ArrayList<>();
    for (Object obj : objects) {
      Map<String, Object> item = (Map<String, Object>) obj;
      YmlModel one = loadOneModel(item);
      mlList.add(one);
    }
    return mlList;
  }

  public static YmlModel loadOneModel(Map<String, Object> map) {
    Object resource = map.get(resource_key);
    if (!(resource instanceof String)) {
      return null;
    }
    if (!Objects.equals(ComponentType.MODEL_DEF.name, resource)) {
      return null;
    }

    // name
    String name = ParserUtils.getStringElseThrow(map, "name");

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
    List<Object> dim1 = (List<Object>) (map.get(dimensions_key));
    List<YmlDimension> dim = parseDimension(dim1);

    return new YmlModel(name, source, joins, columns, dim);
  }

  private static YmlSource parseSource(Map<String, Object> map) {
    String type = ParserUtils.getStringElseThrow(map, source_key_type);

    if (Objects.equals(source_value_type_table, type)) {
      String table = ParserUtils.getStringElseThrow(map, source_key_table);
      String datasource = ParserUtils.getStringElseThrow(map, source_key_datasource);
      return new YmlSourceTable(datasource, table);
    }

    if (Objects.equals(source_value_type_model, type)) {
      String modelName = ParserUtils.getStringElseThrow(map, source_key_model_name);
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
      String modelName = ParserUtils.getStringElseThrow(j, "model_name");
      String joinType = ParserUtils.getStringElse(j, "join_type", JoinType.INNER.name);
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
      String name = ParserUtils.getStringElseThrow(col, "name");
      String expr = ParserUtils.getStringElse(col, "expr", name);
      String type = ParserUtils.getStringElse(col, "data_type", null);
      String hint = ParserUtils.getStringElse(col, "hint", YmlColumn.HINT_BASIC);
      columns.add(new YmlColumn(name, expr, type, hint));
    }
    return columns;
  }

  private static List<YmlDimension> parseDimension(List<Object> list) {
    if (list == null || list.isEmpty()) {
      return EMPTY_LIST;
    }

    List<YmlDimension> dims = new ArrayList<>(list.size());
    Object obj = list.get(0);
    if (obj instanceof String) {
      for (Object name : list) {
        dims.add(new YmlDimension((String) name, null, null));
      }
    } else if (obj instanceof Map) {
      for (Object kv : list) {
        Map<String, Object> dim = (Map<String, Object>) kv;
        String name = ParserUtils.getStringElseThrow(dim, "name");
        String expr = ParserUtils.getStringElse(dim, "expr", name);
        String type = ParserUtils.getStringElse(dim, "type", null);
        dims.add(new YmlDimension(name, expr, type));
      }
    }

    return dims;
  }

}
