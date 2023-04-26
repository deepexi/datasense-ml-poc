package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.expression.IdentifierPolicy;
import lombok.Getter;

@Getter
public class SqlGeneratorPgContext extends SqlGeneratorContext {

  public SqlGeneratorPgContext(AstNode root) {
    super(root, SqlDialect.POSTGRES, IdentifierPolicy.NO_QUOTE);
  }
}