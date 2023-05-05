package com.deepexi.ds.ast.visitor.generator;

import static com.deepexi.ds.ast.visitor.generator.UdfFunctionFactory.UDF_PREFIX;

public class UdfFunctionCreateDate extends UdfFunction {

  public static final String function_name = UDF_PREFIX + "create_date";

  static {
    UdfFunctionFactory.register(function_name, UdfFunctionCreateDate.class);
  }

  @Override
  public String getFunName() {
    return function_name;
  }

  @Override
  public String visitNode() {
    return null;
  }
}
