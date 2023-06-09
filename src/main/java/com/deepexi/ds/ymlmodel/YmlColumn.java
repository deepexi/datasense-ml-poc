package com.deepexi.ds.ymlmodel;


import com.deepexi.ds.ModelException.FieldMissException;
import lombok.Getter;

@Getter
public class YmlColumn {

  private final String name;
  private final String expr;
  private final String dataType;
  private final String datePart;

  public YmlColumn(String name, String expr, String dataType, String datePart) {
    if (name == null) {
      throw new FieldMissException("column.name");
    }
    if (expr == null) {
      throw new FieldMissException("column.expr");
    }
    this.name = name;
    this.expr = expr;
    this.dataType = dataType;
    this.datePart = datePart;
  }
}
