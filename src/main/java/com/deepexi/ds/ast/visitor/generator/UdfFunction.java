package com.deepexi.ds.ast.visitor.generator;

import com.deepexi.ds.ast.expression.FunctionExpression;
import lombok.Data;

/**
 * 自定义函数. 这些函数具有 语义特性, 需要翻译成特定的 数据库实现
 */
@Data
public abstract class UdfFunction {

  protected FunctionExpression node;
  protected SqlGeneratorContext context;

  public abstract String getFunName();

  public abstract String visitNode();
}
