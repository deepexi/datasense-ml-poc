package com.deepexi.ds.ast;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class MetricBindQuery extends Relation {

  private final Identifier name;                          // 该 metric 名
  private final Relation source;                          // 该查询所依赖的 "表", from 子句
  private final ImmutableList<Column> columns;            // 列

  // 查询有关的条件
  private final ImmutableList<Expression> having;         // having 子句中的过滤条件
  private final ImmutableList<Column> groupBy;            // group by 子句中的列, 也会在 select中
  private final ImmutableList<Expression> where;          // where clause中的列

  // orderBy / limit /offset
  private final ImmutableList<OrderBy> orderBys;
  private final Integer limit;
  private final Integer offset;

  public MetricBindQuery(
      Identifier queryName,
      Relation source,
      List<Expression> where,
      List<Column> groupBy,
      List<Expression> having,
      List<Column> columns,
      List<OrderBy> orderBys,
      Integer limit,
      Integer offset) {
    this.name = queryName;
    this.source = source;
    this.where = ImmutableList.copyOf(where);
    this.groupBy = ImmutableList.copyOf(groupBy);
    this.having = ImmutableList.copyOf(having);
    this.columns = ImmutableList.copyOf(columns);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.limit = limit;
    this.offset = offset;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitMetricBindQuery(this, context);
  }

  @Override
  public Identifier getTableName() {
    return getName();
  }

  @Override
  public Relation getFrom() {
    return source;
  }

  @Override
  public List<Join> getJoins() {
    return EMPTY_LIST;
  }

  @Override
  public boolean hasAnyColumn() {
    return false;
  }

}
