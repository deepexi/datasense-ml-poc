package com.deepexi.ds.ymlmodel.factory;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ymlmodel.YmlDebug;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.yaml.snakeyaml.Yaml;

public class YmlDebugParser {

  private YmlDebugParser() {
  }

  public static YmlDebug loadOneModel(String resFile) {
    Yaml yaml = new Yaml();
    InputStream inputStream = YmlDebugParser.class.getClassLoader()
        .getResourceAsStream(resFile);
    Map<String, Object> map = yaml.load(inputStream);
    return loadOneModel(map);
  }

  public static YmlDebug loadOneModel(Map<String, Object> map) {
    Object resource = map.get("resource");
    if (!(resource instanceof String)) {
      return null;
    }
    if (!Objects.equals(ComponentType.MODEL_DEBUG.name, resource)) {
      return null;
    }
    String sql = ParserUtils.getStringElseThrow(map, "sql");
    return new YmlDebug(sql);
  }
}
