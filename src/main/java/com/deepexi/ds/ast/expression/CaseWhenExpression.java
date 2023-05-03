package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;

/**
 * <pre>
 * case
 *  (when...then...) 多个
 *  (else expression) 1个或没有
 * end
 * </pre>
 */
@Getter
public class CaseWhenExpression extends Expression {

  private final ImmutableList<WhenThen> whenThenList;
  private final Expression elseExpression;

  public CaseWhenExpression(List<WhenThen> whenThenList, Expression elseExpression) {
    this.whenThenList = ImmutableList.copyOf(whenThenList);
    this.elseExpression = elseExpression;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new TODOException("TODO");
  }

  @Override
  public String toString() {
    String elseStr = elseExpression == null ? "" : "else " + elseExpression.toString();
    StringBuilder whenThenBuilder = new StringBuilder();
    whenThenList.forEach(whenThen -> {
      whenThenBuilder.append(whenThen.toString());
    });
    return String.format("case %s %s end", whenThenBuilder.toString(), elseStr);
  }

  @Getter
  public static class WhenThen extends Expression {

    private final Expression when;
    private final Expression then;

    public WhenThen(Expression when, Expression then) {
      this.when = when;
      this.then = then;
    }

    @Override
    public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
      throw new TODOException("TODO");
    }

    @Override
    public String toString() {
      return String.format("when %s then %s ", when.toString(), then.toString());
    }
  }
}
