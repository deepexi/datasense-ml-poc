package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlJoin {

  private final String modelName;
  private final String joinType;
  // 目前的实现中 condition 全部按照 Logic and 的形式进行 组合
  // 所以存放在一个 List中, 不需要 像 数据库一样 以树结构存储
  private final ImmutableList<String> conditions;

  public YmlJoin(String modelName, String joinType, List<String> conditions) {
    this.modelName = modelName;
    this.joinType = joinType;
    this.conditions = ImmutableList.copyOf(conditions);
  }
}
