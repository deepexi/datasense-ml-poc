package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class Join extends AstNode {

  private final Model model;
  private final JoinType joinType;
  // 目前的实现中 condition 全部按照 Logic and 的形式进行 组合
  // 所以存放在一个 List中, 不需要 像 数据库一样 以树结构存储
  private final ImmutableList<? extends Expression> conditions;

  public Join(Model model, JoinType joinType, List<? extends Expression> conditions) {
    super();
    this.model = model;
    this.joinType = joinType;
    this.conditions = ImmutableList.copyOf(conditions);
  }

  @Override
  public <R, C> R accept(ModelVisitor<R, C> visitor, C context) {
    return visitor.visitJoin(this, context);
  }

  public String toString() {
    return String.format("%s join %s", joinType.name, model.name);
  }
}
