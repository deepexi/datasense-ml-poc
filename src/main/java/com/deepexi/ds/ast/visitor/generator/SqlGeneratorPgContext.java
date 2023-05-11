package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.SqlDialect;
import lombok.Getter;

@Getter
public class SqlGeneratorPgContext extends SqlGeneratorContext {

  public SqlGeneratorPgContext(AstNode root) {
    super(root,
        SqlDialect.POSTGRES,
        IdentifierQuotePolicy.NO_QUOTE,
        // IdentifierShowPolicy.NO_TABLE_NAME
        IdentifierShowPolicy.SHOW_TABLE_NAME
    );
  }

  public SqlGeneratorPgContext(AstNode root, IdentifierShowPolicy showPolicy) {
    super(root,
        SqlDialect.POSTGRES,
        IdentifierQuotePolicy.NO_QUOTE,
        showPolicy
    );
  }
}