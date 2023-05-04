package com.deepexi.ds.ymlmodel.factory;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ymlmodel.YmlFrameBoundary;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlMetricQuery.YmlOrderBy;
import com.deepexi.ds.ymlmodel.YmlWindow;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
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
    List<String> metricFilter = (List<String>) (map.get("metric_filters"));
    if (metricFilter == null) {
      metricFilter = EMPTY_LIST;
    }

    // orderBy
    List<YmlOrderBy> orderBys = parseOrderBy(map);

    // limit
    Integer limit = (Integer) map.get("limit");

    // offset
    Integer offset = (Integer) map.get("offset");

    // window
    YmlWindow window = parseWindow(map);
    return new YmlMetricQuery(
        name,
        metricNames,
        dim,
        modelFilter,
        metricFilter,
        orderBys,
        limit,
        offset,
        window);
  }

  private static List<YmlOrderBy> parseOrderBy(Map<String, Object> mapHasOrderBy) {
    List<YmlOrderBy> orderBys = new ArrayList<>();
    List<Map<String, String>> orderBysObj = (List<Map<String, String>>) mapHasOrderBy.get(
        "order_bys");
    if (orderBysObj != null) {
      orderBysObj.forEach(ele -> {
        String name1 = ele.get("name");
        String direction = ele.get("direction");
        orderBys.add(new YmlOrderBy(name1, direction));
      });
    }
    return orderBys;
  }

  private static YmlWindow parseWindow(Map<String, Object> mapHasWindow) {
    if (!mapHasWindow.containsKey("window")) {
      return null;
    }
    Map<String, Object> windowMap = (Map<String, Object>) mapHasWindow.get("window");
    String windowType = (String) windowMap.get("window_type");
    List<String> partitions = (List<String>) windowMap.get("partitions");
    List<YmlOrderBy> orderBys = parseOrderBy(windowMap);
    String frameType = (String) windowMap.get("frame_type");
    YmlFrameBoundary start = parseBoundary((Map<String, Object>) windowMap.get("start"));
    YmlFrameBoundary end = parseBoundary((Map<String, Object>) windowMap.get("end"));
    //
    return new YmlWindow(windowType, partitions, orderBys, frameType, start, end);
  }

  private static YmlFrameBoundary parseBoundary(Map<String, Object> boundaryInfo) {
    String base = (String) boundaryInfo.get("base");
    int offset = (int) boundaryInfo.get("offset");
    return new YmlFrameBoundary(base, offset);
  }
}
