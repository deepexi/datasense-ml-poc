package com.deepexi.ds.ast.expression;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.DateTimeUnit;
import lombok.Getter;

/**
 * 从 Date/DateTime/Timestamp中 抽取 年月日时分秒
 */
@Getter
public class UdfExtractDatePart extends UdfExpression {

  private static final String NAME = "extract_date_type";
  private final Expression fromWhat;
  private final DateTimeUnit fromUnit;
  private final DateTimeUnit unit;

  public UdfExtractDatePart(Expression fromWhat, DateTimeUnit fromUnit, DateTimeUnit unit) {
    super(NAME, EMPTY_LIST);

    this.fromWhat = fromWhat;
    this.fromUnit = fromUnit;
    this.unit = unit;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    throw new TODOException();
    // return visitor.visitUdfExtractDatePart(this, context);
  }
}
