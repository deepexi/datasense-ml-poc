package com.deepexi.ds;

import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.UdfExpression;
import java.util.Arrays;

public interface DevConfig {

  boolean DEBUG = false;

  // String BASE_DATE = "1970-01-01";
  UdfExpression BASE_DATE_19700101 = new UdfExpression(
      "create_date_by_ymd",
      Arrays.asList(
          new IntegerLiteral(1970),
          new IntegerLiteral(1),
          new IntegerLiteral(1)
      )
  );
}
