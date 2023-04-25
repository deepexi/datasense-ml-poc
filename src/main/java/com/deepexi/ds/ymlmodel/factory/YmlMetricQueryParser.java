package com.deepexi.ds.ymlmodel.factory;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

public class YmlMetricQueryParser {

  private YmlMetricQueryParser() {
  }

  public static YmlMetricQuery loadOneModel(String resFile) {
    Yaml yaml = new Yaml();
    InputStream inputStream = YmlMetricQueryParser.class.getClassLoader()
        .getResourceAsStream(resFile);
    Map<String, Object> map = yaml.load(inputStream);
    return loadOneModel(map);
  }

  public static YmlMetricQuery loadOneModel(Map<String, Object> map) {
    Object resource = map.get("resource");
    if (!(resource instanceof String)) {
      return null;
    }
    if (!Objects.equals(ComponentType.METRICS_QUERY.name, resource)) {
      return null;
    }

    String name = ParserUtils.getStringElseThrow(map, "name");

    // metric_names
    List<String> metricNames = (List<String>) map.get("metric_names");
    if (metricNames == null) {
      metricNames = EMPTY_LIST;
    }

    // dimensions
    List<String> dim = (List<String>) (map.get("dimensions"));
    if (dim == null) {
      dim = EMPTY_LIST;
    }

    // model_filter
    List<String> modelFilter = (List<String>) (map.get("model_filters"));
    if (modelFilter == null) {
      modelFilter = EMPTY_LIST;
    }

    // dimension_filter
    List<String> dimFilter = (List<String>) (map.get("dimension_filters"));
    if (dimFilter == null) {
      dimFilter = EMPTY_LIST;
    }
    return new YmlMetricQuery(name, metricNames, dim, modelFilter, dimFilter);
  }
}
