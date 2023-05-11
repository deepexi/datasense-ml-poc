package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.SqlDialect;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class SqlGeneratorContext {

  protected final AstNode root;
  protected final SqlDialect sqlDialect;
  protected final IdentifierQuotePolicy quotePolicy;
  protected final IdentifierShowPolicy showPolicy;
  protected final AtomicInteger sequence;

  public SqlGeneratorContext(
      AstNode root,
      SqlDialect sqlDialect,
      IdentifierQuotePolicy policy,
      IdentifierShowPolicy showPolicy
  ) {
    Objects.requireNonNull(root, "Model %s not found in QueryContext");
    Objects.requireNonNull(sqlDialect, "dialect should provide");

    this.root = root;
    this.sqlDialect = sqlDialect;
    this.quotePolicy = policy;
    this.showPolicy = showPolicy;
    this.sequence = new AtomicInteger(0);
  }

  public int nextId() {
    return sequence.getAndIncrement();
  }
}