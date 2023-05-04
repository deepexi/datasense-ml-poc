package com.deepexi.ds.ymlmodel;

import com.deepexi.ds.ymlmodel.YmlMetricQuery.YmlOrderBy;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

@Getter
public class YmlWindow {

  private final String windowType;
  private final ImmutableList<String> partitions;
  private final ImmutableList<YmlOrderBy> orderBys;
  private final String frameType;
  private final YmlFrameBoundary start;
  private final YmlFrameBoundary end;

  public YmlWindow(
      String windowType,
      List<String> partitions,
      List<YmlOrderBy> orderBys,
      String frameType,
      YmlFrameBoundary start,
      YmlFrameBoundary end) {
    this.windowType = windowType;
    this.partitions = ImmutableList.copyOf(partitions);
    this.orderBys = ImmutableList.copyOf(orderBys);
    this.frameType = frameType;
    this.start = start;
    this.end = end;
  }
}
