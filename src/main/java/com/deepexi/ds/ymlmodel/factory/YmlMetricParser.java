package com.deepexi.ds.ymlmodel.factory;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ymlmodel.YmlMetric;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class YmlMetricParser {

  private YmlMetricParser() {
  }

  public static YmlMetric loadOneModel(String resFile) {
    Yaml yaml = new Yaml();
    InputStream inputStream = YmlMetricParser.class.getClassLoader().getResourceAsStream(resFile);
    Map<String, Object> map = yaml.load(inputStream);
    return loadOneModel(map);
  }

  public static List<YmlMetric> loadModels(String resFile) {
    Yaml yaml = new Yaml();

    InputStream input = YmlMetricParser.class.getClassLoader().getResourceAsStream(resFile);
    Iterable<Object> objects = yaml.loadAll(input);
    List<YmlMetric> mlList = new ArrayList<>();
    for (Object obj : objects) {
      Map<String, Object> item = (Map<String, Object>) obj;
      YmlMetric one = loadOneModel(item);
      mlList.add(one);
    }
    return mlList;
  }

  public static YmlMetric loadOneModel(Map<String, Object> map) {
    Object resource = map.get("resource");
    if (!(resource instanceof String)) {
      return null;
    }
    if (!Objects.equals(ComponentType.METRICS_DEF.name, resource)) {
      return null;
    }

    String name = ParserUtils.getStringElseThrow(map, "name");
    String modelName = ParserUtils.getStringElseThrow(map, "model_name");
    String agg = ParserUtils.getStringElseThrow(map, "agg");
    String dataType = ParserUtils.getStringElse(map, "dataType", null);
    // dimensions
    List<String> dim = (List<String>) (map.get("dimensions"));
    if (dim == null) {
      dim = EMPTY_LIST;
    }
    return new YmlMetric(name, modelName, dim, agg, dataType);
  }
}
