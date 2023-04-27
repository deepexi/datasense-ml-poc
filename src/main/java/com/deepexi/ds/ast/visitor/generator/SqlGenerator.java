package com.deepexi.ds.ast.visitor.generator;

import static com.deepexi.ds.ast.utils.SqlTemplateId.metric_bind_query_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.model_001;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Dimension;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.RelationFromModel;
import com.deepexi.ds.ast.RelationFromModelSource;
import com.deepexi.ds.ast.RelationFromTableSource;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IdentifierPolicy;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.condition.BinaryExpression;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.Source;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ast.utils.ResUtils;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

/**
 * this visit traverse the whole ModelML, then generate full sql
 */
public class SqlGenerator implements AstNodeVisitor<String, SqlGeneratorContext> {

  @Override
  public String visitMetricBindQuery(MetricBindQuery node, SqlGeneratorContext context) {
    String modelSql = process(node.getModel(), context);
    String aliasSql = process(node.getModel().getName(), context);

    String whereSql = ""; // where optional
    if (node.getModelFilters().size() > 0) {
      StringBuilder whereBuilder = new StringBuilder();
      whereBuilder.append("where ");
      for (int i = 0; i < node.getModelFilters().size(); i++) {
        Expression ele = node.getModelFilters().get(i);
        String oneWhere = process(ele, context);
        if (i > 0) {
          whereBuilder.append(" and ");
        }
        whereBuilder.append(oneWhere);
      }
      whereSql = whereBuilder.toString();
    }

    // groupBy
    StringBuilder groupByBuilder = new StringBuilder();
    for (int i = 0; i < node.getDimensions().size(); i++) {
      Dimension ele = node.getDimensions().get(i);
      String expr = ele.getRawExpr(); // TODO 仅有表达式
      if (i > 0) {
        groupByBuilder.append(", ");
      }
      groupByBuilder.append(expr);
    }
    String groupBySql = groupByBuilder.toString();

    // selectSql
    StringBuilder selectBuilder = new StringBuilder();
    for (int i = 0; i < node.getDimensions().size(); i++) {
      Dimension ele = node.getDimensions().get(i);
      String oneDim = process(ele, context); // 有别名
      if (i > 0) {
        selectBuilder.append(", \n");
      }
      selectBuilder.append(oneDim);
    }
    for (int i = 0; i < node.getMetrics().size(); i++) {
      Column column = node.getMetrics().get(i);
      String colStr = process(column, context);
      selectBuilder.append(", \n").append(colStr);
    }
    String selectSql = selectBuilder.toString();

    // havingSql
    String havingSql = "";
    if (node.getMetricFilters().size() > 0) {
      StringBuilder havingBuilder = new StringBuilder();
      havingBuilder.append("having ");
      for (int i = 0; i < node.getMetricFilters().size(); i++) {
        if (i > 0) {
          havingBuilder.append(" and ");
        }
        Expression expression = node.getMetricFilters().get(i);
        String expr = process(expression, context);
        havingBuilder.append(expr);
      }
      havingSql = havingBuilder.toString();
    }
    // 组装模板
    final String sqlTemplate = ResUtils.getSqlTemplate(metric_bind_query_001,
        context.getSqlDialect());
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("aliasSql", aliasSql);
    valuesMap.put("modelSql", ResUtils.indent(modelSql));
    valuesMap.put("selectSql", ResUtils.indent(selectSql));
    valuesMap.put("whereSql", whereSql);
    valuesMap.put("groupBySql", groupBySql);
    valuesMap.put("havingSql", havingSql);

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sqlTemplate);
  }

  @Override
  public String visitModel(Model node, SqlGeneratorContext context) {
    final String ALL_COLUMN = "*";

    // sourceAlias
    Source source = node.getSource();
    Identifier alias = source.getAlias();
    String aliasSql = process(alias, context);

    // sourceSql
    String sourceSql = process(source, context);

    // selectSql
    String selectSql = null;
    List<Column> columns = node.getColumns();
    if (columns == null || columns.size() == 0) {
      selectSql = ALL_COLUMN;
    } else {
      // get = "colA, colB, colC"
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < columns.size(); i++) {
        Column col = columns.get(i);
        String oneColStr = process(col, context);
        builder.append(oneColStr);
        if (i < columns.size() - 1) {
          builder.append(", \n");
        }
      }
      selectSql = builder.toString();
    }

    // joinSql
    String joinSql = ""; // no join
    if (node.getJoins() != null) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < node.getJoins().size(); i++) {
        Join aJoin = node.getJoins().get(i);
        String joinStr = process(aJoin, context);
        builder.append(joinStr);
      }
      joinSql = builder.toString();
    }

    // 组装模板
    final String sqlTemplate = ResUtils.getSqlTemplate(model_001, context.getSqlDialect());
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("aliasSql", aliasSql);
    valuesMap.put("sourceSql", ResUtils.indent(sourceSql));
    valuesMap.put("selectSql", ResUtils.indent(selectSql));
    valuesMap.put("joinSql", joinSql);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sqlTemplate);
  }

  @Override
  public String visitNode(AstNode node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitColumn(Column node, SqlGeneratorContext context) {
    final String pattern = "%s as %s";
    String alias = node.getAlias();
    if (node.getExpr() == null) {
      return node.getRawExpr(); // use rawExpr
    }
    String expr = process(node.getExpr(), context);
    return String.format(pattern, expr, alias);
  }

  @Override
  public String visitJoin(Join node, SqlGeneratorContext context) {
    final String pattern = "\n%s join %s";
    String beforeCondition = String.format(pattern, node.getJoinType().name,
        node.getModel().getName().getValue());
    ImmutableList<? extends Expression> conditions = node.getConditions();
    if (conditions == null || conditions.size() == 0) {
      return beforeCondition;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(beforeCondition).append(" on ");
    for (int i = 0; i < conditions.size(); i++) {
      if (i > 0) {
        builder.append(" and ");
      }
      String aCondition = process(conditions.get(i), context);
      builder.append(aCondition);
    }
    return builder.toString();
  }

  @Override
  public String visitDimension(Dimension node, SqlGeneratorContext context) {
    // throw new ModelException("TODO");
    String pattern = "%s as %s";
    String alias = node.getName();
    String exprStr = node.getRawExpr();

    if (node.getExpr() != null) {
      exprStr = process(node.getExpr(), context);
    }
    return String.format(pattern, exprStr, alias);
  }

  @Override
  public String visitSource(Source node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitTableSource(TableSource node, SqlGeneratorContext context) {
    final String TEMPLATE = "select * from ${table}";

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("table", node.getTableName().getValue());
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(TEMPLATE);
  }

  @Override
  public String visitModelSource(ModelSource node, SqlGeneratorContext context) {
    Model modelML = node.getModel();
    return visitModel(modelML, context);
  }

  @Override
  public String visitStringLiteral(StringLiteral node, SqlGeneratorContext context) {
    return node.getValue();
  }

  @Override
  public String visitIntegerLiteral(IntegerLiteral node, SqlGeneratorContext context) {
    return node.getValue() + "";
  }

  @Override
  public String visitCompareExpression(BinaryExpression node, SqlGeneratorContext context) {
    String left = process(node.getLeft(), context);
    String right = process(node.getRight(), context);
    return left + node.getOp().name + right;
  }

  @Override
  public String visitRelationFromModel(RelationFromModel node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitRelationFromModelSource(RelationFromModelSource node,
      SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitRelationFromTableSource(RelationFromTableSource node,
      SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitIdentifier(Identifier node, SqlGeneratorContext context) {
    IdentifierPolicy policy = context.getIdentifierPolicy();

    String prefix = node.getPrefix();
    String value = node.getValue();
    if (prefix == null) {
      if (policy.hasQuote()) {
        return String.format("%s%s%s",
            policy.quoteString(), value, policy.quoteString());
      } else {
        return String.format("%s", node.getValue());
      }
    }

    if (policy.hasQuote()) {
      return String.format("%s%s%s.%s%s%s",
          policy.quoteString(), prefix, policy.quoteString(),
          policy.quoteString(), value, policy.quoteString());
    } else {
      return String.format("%s.%s", prefix, node.getValue());
    }

  }


  @Override
  public String visitExpression(Expression node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }
}
