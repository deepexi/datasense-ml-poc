package com.deepexi.ds.ymlmodel.factory;

import com.deepexi.ds.ModelException.IllegalYamlFileException;
import com.deepexi.ds.ymlmodel.YmlMetric;
import com.deepexi.ds.ymlmodel.YmlMetricQuery;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

public class HybridComponentParser {

  private HybridComponentParser() {
  }

  @Getter
  public static class SelfContainedContext {

    private final YmlMetricQuery query;
    private final ImmutableList<YmlMetric> metrics;
    private final ImmutableList<YmlModel> models;

    public SelfContainedContext(YmlMetricQuery query, List<YmlMetric> metrics,
        List<YmlModel> models) {
      this.query = query;
      this.metrics = ImmutableList.copyOf(metrics);
      this.models = ImmutableList.copyOf(models);
    }
  }

  public static SelfContainedContext loadFromRes(String resFile) {
    Yaml yaml = new Yaml();
    YmlMetricQuery query = null;
    final List<YmlMetric> metrics = new ArrayList<>();
    final List<YmlModel> models = new ArrayList<>();

    InputStream input = YmlModelParser.class.getClassLoader().getResourceAsStream(resFile);
    Iterable<Object> objects = yaml.loadAll(input);
    for (Object obj : objects) {
      Map<String, Object> item = (Map<String, Object>) obj;
      //
      YmlModel model = YmlModelParser.loadOneModel(item);
      if (model != null) {
        models.add(model);
        continue;
      }
      //
      YmlMetric metric = YmlMetricParser.loadOneModel(item);
      if (metric != null) {
        metrics.add(metric);
        continue;
      }
      //
      YmlMetricQuery query1 = YmlMetricQLParser.loadOneModel(item);
      if (query1 == null) {
        System.out.println("skip something: yml has something not belongs to model");
        continue;
      }
      if (query != null) {
        throw new IllegalYamlFileException("has more than 1 YmlMetricQuery");
      }
      query = query1;
    }

    return new SelfContainedContext(query, metrics, models);
  }
}
