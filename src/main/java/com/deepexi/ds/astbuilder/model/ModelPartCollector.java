package com.deepexi.ds.astbuilder.model;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Identifier;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 在处理某个 Model时, 收集信息
 */
@Getter
class ModelPartCollector {

  private Identifier name;                                    // 当前处理的 Model name
  private Relation source;                                    // 当前处理的 Model source
  private final List<Join> joins = new ArrayList<>();         // 当前处理的 Model joins
  private final List<Column> columns = new ArrayList<>();     // 当前处理的 Model 暴露的 column
  private final List<Column> dimensions = new ArrayList<>();  // 当前处理的 Model 所支持的 dimension

  // scope = source + joins, 指当前Model可见的 table
  private final List<Relation> scopes = new ArrayList<>();

  Model build() {
    return new Model(name, source, joins, columns, dimensions);
  }

  void addSource(Relation r) {
    if (this.source != null) {
      throw new ModelException("one model should not have more than 1 source");
    }
    this.source = r;
    scopes.add(r);
  }

  public void addScope(Relation r) {
    scopes.add(r);
  }

  void addJoin(Join join) {
    joins.add(join);
  }

  public void setName(Identifier name) {
    this.name = name;
  }

  public void addColumn(Column column) {
    this.columns.add(column);
  }

  public void addDimension(Column dim) {
    this.dimensions.add(dim);
  }

}
