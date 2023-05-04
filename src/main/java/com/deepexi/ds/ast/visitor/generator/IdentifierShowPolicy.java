package com.deepexi.ds.ast.visitor.generator;

/**
 * 在使用列时, 是否带上 表名
 */
public interface IdentifierShowPolicy {

  boolean showTableName();

  default boolean showSchemaName() {
    return false;
  }

  IdentifierShowPolicy SHOW_TABLE_NAME = new IdentifierShowTable();
  IdentifierShowPolicy NO_TABLE_NAME = new IdentifierNotShowTable();

  public class IdentifierShowTable implements IdentifierShowPolicy {

    @Override
    public boolean showTableName() {
      return true;
    }
  }

  public class IdentifierNotShowTable implements IdentifierShowPolicy {

    @Override
    public boolean showTableName() {
      return false;
    }
  }
}
