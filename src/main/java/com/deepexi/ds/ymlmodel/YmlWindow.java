package com.deepexi.ds.ymlmodel;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlWindow {

  private final String windowType;
  private final int length;
  private final String dimension;
  private final ImmutableList<String> partitions;
  private final ImmutableList<String> orderBy;

  public YmlWindow(
      String windowType,
      int length,
      String dimension,
      List<String> partitions,
      List<String> orderBy) {
    this.windowType = windowType;
    this.length = length;
    this.dimension = dimension;
    this.partitions = ImmutableList.copyOf(partitions);
    this.orderBy = ImmutableList.copyOf(orderBy);
  }
}
