package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.expression.IdentifierPolicy.IdentifierPolicyNoQuote;
import lombok.Getter;

@Getter
public class SqlGeneratorPgContext extends SqlGeneratorContext {

  public SqlGeneratorPgContext(Model root) {
    super(root, SqlDialect.POSTGRES, IdentifierPolicyNoQuote.INSTANCE);
  }
}