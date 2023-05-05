package com.deepexi.ds.ast.visitor.generator;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.expression.FunctionExpression;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义函数. 这些函数具有 语义特性, 需要翻译成特定的 数据库实现
 */
public class UdfFunctionFactory {

  public static final String UDF_PREFIX = "ml_udf_";

  public static boolean isUdf(FunctionExpression node) {
    return node != null && node.getName().startsWith(UDF_PREFIX);
  }

  public static UdfFunction get(FunctionExpression node, SqlGeneratorContext context) {
    String name = node.getName();
    Class<? extends UdfFunction> clazz = registry.get(name);
    if (clazz == null) {
      throw new ModelException("TODO: 不支持 自定义函数 " + name);
    }

    UdfFunction udfFunction = null;
    try {
      udfFunction = clazz.newInstance();
    } catch (InstantiationException e) {
      throw new ModelException("TODO: 不支持 自定义函数 " + name);
    } catch (IllegalAccessException e) {
      throw new ModelException("TODO: 不支持 自定义函数 " + name);
    }
    udfFunction.setContext(context);
    udfFunction.setNode(node);
    return udfFunction;
  }

  public static void register(String name, Class<? extends UdfFunction> clazz) {
    registry.put(name, clazz);
  }

  private static Map<String, Class<? extends UdfFunction>> registry = new HashMap<>();

}
