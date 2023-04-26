package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.expression.IdentifierPolicy;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class SqlGeneratorContext {

  protected final AstNode root;
  private final SqlDialect sqlDialect;
  private final IdentifierPolicy identifierPolicy;
  private final AtomicInteger sequence;

  public SqlGeneratorContext(AstNode root, SqlDialect sqlDialect, IdentifierPolicy policy) {
    Objects.requireNonNull(root, "Model %s not found in QueryContext");
    Objects.requireNonNull(sqlDialect, "dialect should provide");

    this.root = root;
    this.identifierPolicy = policy;
    this.sqlDialect = sqlDialect;
    this.sequence = new AtomicInteger(0);
  }

  public int nextId() {
    return sequence.getAndIncrement();
  }
}