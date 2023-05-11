package com.deepexi.ds.ast.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.visitor.analyzer.ScopeCollector;
import com.deepexi.ds.ast.visitor.analyzer.ScopeCollectorContext;
import com.deepexi.ds.astbuilder.model.ModelBuilder;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.factory.YmlModelParser;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScopeCollectorTest {

  @Test
  void testVisitModel() {
    List<YmlModel> ymlModels = YmlModelParser.loadModels("debug/join_2_models.yml");
    assertEquals(3, ymlModels.size());
    Model rootModel = ModelBuilder.singleTreeModel(ymlModels);

    // 准备 visit
    ScopeCollectorContext context = new ScopeCollectorContext(rootModel);
    ScopeCollector collector = new ScopeCollector();
    collector.process(rootModel, context);
    // size = 所有Source节点数 + 所有 model_ml节点数
    // 这里 共有 5个 relation, 还有一个join也被存储了
    // model_ml (name=join1)
    // model_ml (name=store)
    // model_ml (name=store_sales)
    // source (table=store)
    // source (table=store_sales)
    assertEquals(context.getRegistry().size(), 6);
  }
}
