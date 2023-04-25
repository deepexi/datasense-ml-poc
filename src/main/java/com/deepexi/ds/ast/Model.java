package com.deepexi.ds.ast;

import com.deepexi.ds.ComponentType;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.source.Source;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class Model extends AstComponent {

  protected final Identifier name;
  protected final Source source;
  protected final ImmutableList<Join> joins;
  protected final ImmutableList<Column> columns;
  protected final ImmutableList<Dimension> dimensions;

  public Model(Identifier name, Source source, List<Join> joins, List<Column> columns,
      List<Dimension> dimensions) {
    this.name = name;
    this.columns = ImmutableList.copyOf(columns);
    this.dimensions = ImmutableList.copyOf(dimensions);
    this.joins = ImmutableList.copyOf(joins);
    this.source = source;
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitModel(this, context);
  }

  @Override
  public ComponentType getComponentType() {
    return ComponentType.MODEL_DEF;
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
