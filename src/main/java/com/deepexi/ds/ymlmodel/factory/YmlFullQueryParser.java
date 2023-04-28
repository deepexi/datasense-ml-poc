package com.deepexi.ds.ymlmodel.factory;

import com.deepexi.ds.ModelException.IllegalYamlFileException;
import com.deepexi.ds.ymlmodel.YmlDebug;
import com.deepexi.ds.ymlmodel.YmlFullQuery;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlModel;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class YmlFullQueryParser {

  private YmlFullQueryParser() {
  }

  public static YmlFullQuery loadFromRes(String resFile) {
    Yaml yaml = new Yaml();

    YmlMetricQuery query = null;
    final List<YmlMetric> metrics = new ArrayList<>();
    final List<YmlModel> models = new ArrayList<>();
    YmlDebug ymlDebug = null;

    InputStream input = YmlModelParser.class.getClassLoader().getResourceAsStream(resFile);
    Iterable<Object> objects = yaml.loadAll(input);
    for (Object obj : objects) {
      if (!(obj instanceof Map)) {
        System.out.println("skip something: yml has something not belongs to model");
        continue;
      }

      Map<String, Object> item = (Map<String, Object>) obj;
      // YmlModel
      YmlModel model = YmlModelParser.loadOneModel(item);
      if (model != null) {
        models.add(model);
        continue;
      }
      // YmlMetric
      YmlMetric metric = YmlMetricParser.loadOneModel(item);
      if (metric != null) {
        metrics.add(metric);
        continue;
      }
      // YmlMetricQuery
      YmlMetricQuery query1 = YmlMetricQueryParser.loadOneModel(item);
      if (query1 != null) {
        if (query != null) {
          throw new IllegalYamlFileException("has more than 1 YmlMetricQuery");
        }
        query = query1;
        continue;
      }

      //
      YmlDebug ymlDebug1 = YmlDebugParser.loadOneModel(item);
      if (ymlDebug1 != null) {
        ymlDebug = ymlDebug1;
        continue;
      }

      // something we dont know, just ignore
      System.out.println("skip something: yml has something not belongs to model");
    }

    return new YmlFullQuery(query, metrics, models, ymlDebug);
  }
}
