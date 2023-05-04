package com.deepexi.ds.ast.visitor.generator;

import static com.deepexi.ds.ast.utils.SqlTemplateId.case_when_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.metric_bind_query_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.model_001;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.Window;
import com.deepexi.ds.ast.expression.BooleanLiteral;
import com.deepexi.ds.ast.expression.CaseWhenExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression.WhenThen;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IdentifierPolicy;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.source.ModelSource;
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
    String modelSql = process(node.getRelation(), context);
    String aliasSql = process(node.getRelation().getTableName(), context);

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
      Column ele = node.getDimensions().get(i);
      String expr = process(ele.getExpr(), context); // TODO 仅有表达式
      if (i > 0) {
        groupByBuilder.append(", ");
      }
      groupByBuilder.append(expr);
    }
    String groupBySql = groupByBuilder.toString();

    // selectSql
    StringBuilder selectBuilder = new StringBuilder();
    for (int i = 0; i < node.getDimensions().size(); i++) {
      Column ele = node.getDimensions().get(i);
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

    // order by
    String orderBySql = "";
    if (node.getOrderBys().size() > 0) {
      StringBuilder orderByBuilder = new StringBuilder();
      orderByBuilder.append("order by ");
      for (int i = 0; i < node.getOrderBys().size(); i++) {
        if (i > 0) {
          orderByBuilder.append(", ");
        }
        String str = process(node.getOrderBys().get(i), context);
        orderByBuilder.append(str);
      }
      orderBySql = orderByBuilder.toString();
    }

    // limit offset
    String limitSql = node.getLimit() == null ? "" : "limit " + node.getLimit();
    String offsetSql = node.getOffset() == null ? "" : "offset " + node.getOffset();
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
    valuesMap.put("orderBySql", orderBySql);
    valuesMap.put("limitSql", limitSql);
    valuesMap.put("offsetSql", offsetSql);

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sqlTemplate).trim();
  }

  @Override
  public String visitOrderBy(OrderBy node, SqlGeneratorContext context) {
    String tblName = node.getName().getPrefix();
    String colName = node.getName().getValue();
    String direction = node.getDirection().name;
    return String.format("%s.%s %s", tblName, colName, direction);
  }

  @Override
  public String visitCaseWhen(CaseWhenExpression node, SqlGeneratorContext context) {
    ImmutableList<WhenThen> whenThenList = node.getWhenThenList();
    StringBuilder whenThenBuilder = new StringBuilder();
    whenThenList.forEach(whenThen -> {
      String whenThenSql = process(whenThen, context);
      whenThenBuilder.append(whenThenSql);
    });
    String whenThenSql = whenThenBuilder.toString();

    String elseSql = "";
    if (node.getElseExpression() != null) {
      elseSql = "else " + process(node.getElseExpression(), context);
    }

    // 组装模板
    final String sqlTemplate = ResUtils.getSqlTemplate(case_when_001, context.getSqlDialect());
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("whenThenSql", whenThenSql);
    valuesMap.put("elseSql", elseSql);

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sqlTemplate).trim();
  }

  @Override
  public String visitWhenThen(WhenThen node, SqlGeneratorContext context) {
    String whenSql = process(node.getWhen(), context);
    String thenSql = process(node.getThen(), context);
    return String.format("when %s then %s ", whenSql, thenSql);
  }

  @Override
  public String visitBooleanLiteral(BooleanLiteral node, SqlGeneratorContext context) {
    if (node == BooleanLiteral.TRUE) {
      return "true";
    } else {
      return "false";
    }
  }

  @Override
  public String visitFunction(FunctionExpression node, SqlGeneratorContext context) {
    String pattern = "_fun_(_args_)";
    String s1 = pattern.replace("_fun_", node.getName());
    String args = "";
    if (node.getArgs().size() > 0) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < node.getArgs().size(); i++) {
        if (i > 0) {
          builder.append(", ");
        }
        Expression arg = node.getArgs().get(i);
        builder.append(process(arg, context));
      }
      args = builder.toString();
    }
    return s1.replace("_args_", args);
  }

  @Override
  public String visitWindow(Window node, SqlGeneratorContext context) {
    throw new RuntimeException("TODO");
  }

  @Override
  public String visitModel(Model node, SqlGeneratorContext context) {
    final String ALL_COLUMN = "*";

    // sourceAlias
    Relation source = node.getSource();
    Identifier alias = source.getTableName();
    String aliasSql = process(alias, context);

    // sourceSql
    String sourceSql = process(source, context);

    // selectSql
    String selectSql;
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
      throw new ModelException("column expression should not be null");
      // return node.getRawExpr(); // use rawExpr
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
    return String.valueOf(node.getValue());
  }

  @Override
  public String visitCompareExpression(CompareExpression node, SqlGeneratorContext context) {
    String left = process(node.getLeft(), context);
    String right = process(node.getRight(), context);
    return left + node.getOp().name + right;
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
