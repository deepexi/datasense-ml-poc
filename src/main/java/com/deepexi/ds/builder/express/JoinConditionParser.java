package com.deepexi.ds.builder.express;

import com.deepexi.ds.ModelException.TODOException;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.builder.ModelBuilder;
import com.deepexi.ds.builder.RelationMock;
import com.deepexi.ds.parser.ParserUtils;
import java.util.List;

/**
 * join 中的条件(on 中的部分) parser
 * on tableA.colX = tableB. colY  ===> CompareExpression
 */
public class JoinConditionParser {

  private final String literal;
  private final List<RelationMock> scope;
  private final RelationMock sourceRelation;

  public JoinConditionParser(String literal, List<RelationMock> scope,
      RelationMock sourceRelation) {
    this.literal = literal.trim();
    this.scope = scope;
    this.sourceRelation = sourceRelation;
  }

  public Expression parse() {
    return tryParse();
  }

  private Expression tryParse() {
    Expression expr = ParserUtils.parseBooleanExpression(literal);

    if (expr instanceof CompareExpression) {
      // 确保 left/right所在的 relation 存在 TODO 这个校验应该留在一个完整的 visitor中进行

      Expression left = ((CompareExpression) expr).getLeft();
      if (left instanceof Identifier) {
        assertTableHasColumn((Identifier) left);
      }

      Expression right = ((CompareExpression) expr).getRight();
      if (right instanceof Identifier) {
        assertTableHasColumn((Identifier) right);
      }
      return expr;
    }
    throw new TODOException("TODO");
  }

  private void assertTableHasColumn(Identifier tableCol) {
    String tableName = tableCol.getPrefix();
    String colName = tableCol.getValue();
    if (tableName != null) {
      RelationMock rel = ModelBuilder.assertTableInScope(tableName, scope);
      ModelBuilder.assertColumnExistsInRelation(colName, rel);
    } else {
      ModelBuilder.assertColumnExistsInRelation(colName, sourceRelation);
    }
  }
}
