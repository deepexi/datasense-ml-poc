package com.deepexi.ds.ymlmodel.factory;

import com.deepexi.ds.ModelException;
import java.util.Map;

class ParserUtils {

  public static String getStringElseThrow(Map<String, Object> map, String key) {
    Object val = map.get(key);
    if (val == null) {
      throw new ModelException(String.format("key [%s] not found in map", key));
    }
    if (!(val instanceof String)) {
      throw new ModelException(String.format("value of %s is not String", key));
    }
    return (String) (val);
  }

  public static String getStringElse(Map<String, Object> map, String key, String defVal) {
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
