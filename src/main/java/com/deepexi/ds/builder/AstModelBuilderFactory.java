package com.deepexi.ds.builder;

import com.deepexi.ds.ModelException.ModelHasCycleException;
import com.deepexi.ds.ModelException.ModelHasManyRootException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ModelException.NoModelException;
import com.deepexi.ds.yml2pojo.YmlJoin;
import com.deepexi.ds.yml2pojo.YmlModel;
import com.deepexi.ds.yml2pojo.YmlSource;
import com.deepexi.ds.yml2pojo.YmlSourceModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AstModelBuilderFactory {

//  @Getter
//  private static class YmlNodeRef {
//
//    private String self;
//    private List<String> depends = new ArrayList<>();
//    private AtomicInteger referCount = new AtomicInteger(0);
//
//    public YmlNodeRef(String self) {
//      this.self = self;
//    }
//
//    public void addDepend(String depend) {
//      depends.add(depend);
//    }
//
//    public void incRefer() {
//      referCount.incrementAndGet();
//    }
//  }

  private final List<YmlModel> models;
  private final Map<String, YmlModel> lookup = new HashMap<>();
  //  private final Map<String, YmlNodeRef> record = new HashMap<>();
  private final List<String> leafNode = new ArrayList<>();
  private MutableGraph<String> graph = GraphBuilder.directed().build();

  public AstModelBuilderFactory(List<YmlModel> models) {
    if (models == null || models.size() == 0) {
      throw new NoModelException();
    }
    this.models = models;
    for (YmlModel y : models) {
      lookup.put(y.getName(), y);
      graph.addNode(y.getName());
    }
  }

  private void addDepend(String self, String depend) {
    YmlModel selfNode = lookup.get(self);
    if (selfNode == null) {
      throw new ModelNotFoundException(self);
    }
    YmlModel dependNode = lookup.get(depend);
    if (dependNode == null) {
      throw new ModelNotFoundException(depend);
    }

//    // self add depend
//    YmlNodeRef ref = record.get(self);
//    if (ref == null) {
//      ref = new YmlNodeRef(self);
//      record.put(self, ref);
//    }
//    ref.addDepend(depend);
//
//    // depend add referCount
//    YmlNodeRef ref2 = record.get(depend);
//    if (ref2 == null) {
//      ref2 = new YmlNodeRef(depend);
//      record.put(depend, ref2);
//    }
//    ref2.incRefer();

    // graph
    graph.putEdge(self, depend);
  }

  private void addLeaf(String self, String externalSource) {
    // since externalSource maybe same name as model, add ns
    YmlModel selfNode = lookup.get(self);
    if (selfNode == null) {
      throw new ModelNotFoundException(self);
    }
    String nodeForExternal = "__ns_external__" + externalSource;

    graph.addNode(nodeForExternal);
    leafNode.add(nodeForExternal);
    graph.putEdge(self, nodeForExternal);
  }

  /**
   * 确认输入的多个 YmlModel仅形成一个tree
   */
  private void parse() {
    for (YmlModel m : models) {
      String self = m.getName();

      // source
      YmlSource source = m.getSource();
      if (source instanceof YmlSourceModel) {
        addDepend(self, source.getAlias());
      } else {
        addLeaf(self, source.getAlias());
      }

      // joins
      ImmutableList<YmlJoin> joins = m.getJoins();
      if (joins != null && joins.size() > 0) {
        joins.forEach(j -> addDepend(self, j.getModelName()));
      }
    }
    // after all finished
  }

  private String assertSingleTree() {
    // 仅有一个节点, referCount = 0, 且这个节点有依赖
    Set<String> inEdgeZero = Sets.newHashSet();
    for (String node : graph.nodes()) {
      if (graph.predecessors(node).isEmpty()) {
        inEdgeZero.add(node);
      }
    }
    ImmutableList<String> rootNode = ImmutableList.copyOf(inEdgeZero);
    if (rootNode.size() != 1) {
      throw new ModelHasManyRootException();
    }
    return rootNode.get(0);
  }

  private void assertNoGraph() {
    if (Graphs.hasCycle(graph)) {
      throw new ModelHasCycleException();
    }
  }

  public AstModelBuilder create() {
    parse();
    assertNoGraph();
    String rootModelName = assertSingleTree();
    YmlModel rootModel = lookup.get(rootModelName);
    return new AstModelBuilder(models, rootModel);
  }
}
