package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.builder.ModelBuilder;
import com.deepexi.ds.builder.RelationMock;
import com.deepexi.ds.parser.ParserUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * join 中的条件(on 中的部分) parser on tableA.colX = tableB. colY  ===> CompareExpression
 * <p>
 * 这个类需要重构, 杂糅了解析 / 校验 / 改写的部分功能
 */
public class BoolConditionParser {

  private final String literal;
  private final ImmutableList<RelationMock> scope;
  private final RelationMock srcRel;

  public BoolConditionParser(String literal, List<RelationMock> scope, RelationMock srcRel) {
    this.literal = literal.trim();
    this.scope = ImmutableList.copyOf(scope);
    this.srcRel = srcRel;
  }

  public Expression parse() {
    return tryParse();
  }

  private Expression tryParse() {
    Expression expr = ParserUtils.parseBooleanExpression(literal);

    if (expr instanceof CompareExpression) {
      // 确保 left/right所在的 relation 存在 TODO 这个校验应该留在一个完整的 visitor中进行

      Expression left = ((CompareExpression) expr).getLeft();
      Expression newLeft = left;
      if (left instanceof Identifier) {
        newLeft = assertTableHasColumn((Identifier) left);
      }

      Expression right = ((CompareExpression) expr).getRight();
      Expression newRight = right;
      if (right instanceof Identifier) {
        newRight = assertTableHasColumn((Identifier) right);
      }
      return new CompareExpression(newLeft, newRight, ((CompareExpression) expr).getOp());
    }
    throw new TODOException("TODO");
  }

  private Identifier assertTableHasColumn(Identifier tableCol) {
    String tableName = tableCol.getPrefix();
    String colName = tableCol.getValue();
    if (tableName != null) {
      RelationMock rel = ModelBuilder.assertTableInScope(tableName, scope);
      ModelBuilder.assertColumnExistsInRelation(colName, rel);
      return tableCol;
    } else {
      ModelBuilder.assertColumnExistsInRelation(colName, srcRel);
      return new Identifier(srcRel.getTableName().getValue(), colName);
    }
  }
}
