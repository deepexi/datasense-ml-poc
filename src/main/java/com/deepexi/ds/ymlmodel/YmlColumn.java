package com.deepexi.ds.ymlmodel;


import com.deepexi.ds.ModelException.FieldMissException;
import java.util.Objects;
import lombok.Getter;

@Getter
public class YmlColumn {

  public static final String HINT_BASIC = "basic";
  public static final String HINT_DERIVED = "derived";

  private final String name;
  private final String expr;
  private final String dataType;
  private final String hint;

  public YmlColumn( String name, String expr, String dataType, String hint) {
    if (name == null) {
      throw new FieldMissException("column.name");
    }
    if (expr == null) {
      throw new FieldMissException("column.expr");
    }
    this.name = name;
    this.expr = expr;
    this.dataType = dataType;
    this.hint = hint;
  }

  public boolean isBasic() {
    return Objects.equals(hint, HINT_BASIC);
  }
}
