package com.deepexi.ds.ast.visitor.generator;


import com.deepexi.ds.ast.BasicContext;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.SqlDialect;
import com.deepexi.ds.ast.expression.IdentifierPolicy;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public class SqlGeneratorContext extends BasicContext {

  private final SqlDialect sqlDialect;
  private final IdentifierPolicy identifierPolicy;
  private final AtomicInteger sequence;

  public SqlGeneratorContext(Model root, SqlDialect sqlDialect,
      IdentifierPolicy identifierPolicy) {
    super(root);
    this.identifierPolicy = identifierPolicy;
    Objects.requireNonNull(sqlDialect, "ModelML %s not found in QueryContext");
    this.sqlDialect = sqlDialect;
    sequence = new AtomicInteger(0);
  }

  public int nextId() {
    return sequence.getAndIncrement();
  }
}