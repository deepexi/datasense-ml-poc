package com.deepexi.ds.ast.visitor.generator;

import static com.deepexi.ds.ast.utils.SqlTemplateId.case_when_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.metric_bind_query_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.model_001;
import static com.deepexi.ds.ast.utils.SqlTemplateId.model_001_cte;
import static com.deepexi.ds.ast.utils.SqlTemplateId.udf_create_date_by_ymd;
import static com.deepexi.ds.ast.utils.SqlTemplateId.window_row_frame_001;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.AstNodeVisitor;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.MetricBindQuery;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.OrderBy;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.ArithmeticExpression;
import com.deepexi.ds.ast.expression.BooleanLiteral;
import com.deepexi.ds.ast.expression.CaseWhenExpression;
import com.deepexi.ds.ast.expression.CaseWhenExpression.WhenThen;
import com.deepexi.ds.ast.expression.CompareExpression;
import com.deepexi.ds.ast.expression.DataTypeLiteral;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.IntegerLiteral;
import com.deepexi.ds.ast.expression.StringLiteral;
import com.deepexi.ds.ast.expression.UdfCastExpression;
import com.deepexi.ds.ast.expression.UdfExpression;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.ast.utils.ResUtils;
import com.deepexi.ds.ast.utils.SqlTemplateId;
import com.deepexi.ds.ast.window.FrameBoundary;
import com.deepexi.ds.ast.window.FrameBoundaryBase;
import com.deepexi.ds.ast.window.FrameType;
import com.deepexi.ds.ast.window.Window;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

/**
 * this visit traverse the whole ModelML, then generate full sql
 */
public class SqlGenerator implements AstNodeVisitor<String, SqlGeneratorContext> {

  private String genOrderBySql(List<OrderBy> orderBys, SqlGeneratorContext context) {
    String orderBySql = "";
    if (orderBys != null && orderBys.size() > 0) {
      StringBuilder orderByBuilder = new StringBuilder();
      orderByBuilder.append("order by ");
      for (int i = 0; i < orderBys.size(); i++) {
        if (i > 0) {
          orderByBuilder.append(", ");
        }
        String str = process(orderBys.get(i), context);
        orderByBuilder.append(str);
      }
      orderBySql = orderByBuilder.toString();
    }
    return orderBySql;
  }

  private String templateFilling(SqlTemplateId templateId, Map<String, String> valuesMap,
      SqlGeneratorContext context) {
    String sqlTemplate = ResUtils.getSqlTemplate(templateId, context.sqlDialect);
    if (sqlTemplate == null) {
      // if sqlTemplate is not found, will throw exception
      throw new ModelException(
          String.format("缺少sql模板, name=%s, dialect=%s", templateId.fileName,
              context.sqlDialect.name));
    }
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(sqlTemplate).trim();
  }

  private String utilCreateCte(Relation r, SqlGeneratorContext context) {
    // cte 包含: from + join
    StringBuilder builder = new StringBuilder();

    // from
    Relation from = r.getFrom();
    String alias = from.getTableName().getValue();
    String fromSql = process(from, context);

    Map<String, String> valuesMap4From = new HashMap<>();
    valuesMap4From.put("alias", alias);
    valuesMap4From.put("querySql", ResUtils.indent(fromSql));
    String cteFrom = templateFilling(model_001_cte, valuesMap4From, context);

    builder.append(cteFrom);

    // join中的 model 也需要一并放到 with中
    for (int i = 0; i < r.getJoins().size(); i++) {
      Model model = r.getJoins().get(i).getModel();
      String joinAlias = model.getTableName().getValue();
      String joinSql = process(model, context).trim();

      Map<String, String> valuesMap = new HashMap<>();
      valuesMap.put("alias", joinAlias);
      valuesMap.put("querySql", ResUtils.indent(joinSql));
      String cteJoin = templateFilling(model_001_cte, valuesMap, context);

      builder.append(", \n");
      builder.append(cteJoin);
    }
    return builder.toString();
  }

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
    String orderBySql = genOrderBySql(node.getOrderBys(), context);
    // limit offset
    String limitSql = node.getLimit() == null ? "" : "limit " + node.getLimit();
    String offsetSql = node.getOffset() == null ? "" : "offset " + node.getOffset();

    // 组装模板
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

    return templateFilling(metric_bind_query_001, valuesMap, context);
  }

  @Override
  public String visitOrderBy(OrderBy node, SqlGeneratorContext context) {
    String field = process(node.getName(), context);
    String direction = node.getDirection().name;
    // like: tableA.colX desc
    return "_field_ _direction_".replace("_field_", field).replace("_direction_", direction);
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
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("whenThenSql", whenThenSql);
    valuesMap.put("elseSql", elseSql);
    return templateFilling(case_when_001, valuesMap, context);
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
  public String visitUdfExpression(UdfExpression node, SqlGeneratorContext context) {
    String funName = node.getName();
    ImmutableList<Expression> args = node.getArgs();

    if (funName.equals("create_date_by_ymd")) {
      // 需要三个参数
      if (args.size() != 3) {
        throw new ModelException("udf " + funName + ": 需三个参数, 当前参数个数=" + args.size());
      }
      String year = process(args.get(0), context);
      String monthOfYear = process(args.get(1), context);
      String dayOfMonth = process(args.get(2), context);

      Map<String, String> valuesMap = new HashMap<>();
      valuesMap.put("year", year);
      valuesMap.put("monthOfYear", monthOfYear);
      valuesMap.put("dayOfMonth", dayOfMonth);
      return templateFilling(udf_create_date_by_ymd, valuesMap, context);
    }
    throw new RuntimeException("不支持的 udf函数:" + funName);
  }

  @Override
  public String visitUdfCastExpression(UdfCastExpression node, SqlGeneratorContext context) {
    String castWhat = process(node.getCastWhat(), context);
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("castWhat", castWhat);

    switch (node.getToType()) {
      case BOOL:
        return templateFilling(SqlTemplateId.udf_cast_to_bool, valuesMap, context);
      case DATE:
        return templateFilling(SqlTemplateId.udf_cast_to_date, valuesMap, context);
      case DATETIME:
        return templateFilling(SqlTemplateId.udf_cast_to_datetime, valuesMap, context);
      case DECIMAL:
        List<Expression> castArgs = node.getCastArgs();
        String castSyntaxError = "cast decimal syntax: udf_function(cast, xxx, decimal, precision, scale)";
        // 参数校验
        if (castArgs.size() != 2) {
          throw new ModelException(castSyntaxError);
        }
        boolean allInteger = castArgs.stream().allMatch(arg -> arg instanceof IntegerLiteral);
        if (!allInteger) {
          throw new ModelException(castSyntaxError);
        }
        IntegerLiteral precision = (IntegerLiteral) castArgs.get(0);
        IntegerLiteral scale = (IntegerLiteral) castArgs.get(1);

        valuesMap.put("precision", String.valueOf(precision.getValue()));
        valuesMap.put("scale", String.valueOf(scale.getValue()));
        return templateFilling(SqlTemplateId.udf_cast_to_decimal, valuesMap, context);
      case INTEGER:
        return templateFilling(SqlTemplateId.udf_cast_to_integer, valuesMap, context);
      case STRING:
        return templateFilling(SqlTemplateId.udf_cast_to_string, valuesMap, context);
      case TIME:
      case TIMESTAMP:
      default:
        throw new RuntimeException("TODO");
    }
  }

  @Override
  public String visitArithmeticExpression(ArithmeticExpression node, SqlGeneratorContext context) {
    String left = process(node.getLeft(), context);
    String right = process(node.getRight(), context);
    String op = node.getOp().name;
    return left + op + right;
  }

  @Override
  public String visitDataTypeLiteral(DataTypeLiteral node, SqlGeneratorContext context) {
    throw new RuntimeException("TODO");
  }

  @Override
  public String visitWindow(Window node, SqlGeneratorContext context) {
    if (node.getFrameType() != FrameType.ROWS) {
      throw new ModelException("TODO, current only support rows");
    }

    // partitionSql
    String partitionSql = "";
    if (node.getPartitions() != null && node.getPartitions().size() > 0) {
      ImmutableList<Identifier> partitions = node.getPartitions();
      StringBuilder builder = new StringBuilder();
      builder.append("partition by ");
      for (int i = 0; i < partitions.size(); i++) {
        if (i > 0) {
          builder.append(", ");
        }
        Identifier identifier = partitions.get(i);
        builder.append(process(identifier, context));    // 用 prefix.value
      }
      partitionSql = builder.toString();
    }

    String orderBySql = genOrderBySql(node.getOrderBys(), context);
    String frameStart = "";
    if (node.getFrameStart() != null) {
      frameStart = process(node.getFrameStart(), context);
    }

    String frameEnd = "";
    if (node.getFrameEnd() != null) {
      frameEnd = process(node.getFrameEnd(), context);
    }
    String frameType = node.getFrameType().name;

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("partitionSql", partitionSql);
    valuesMap.put("orderBySql", orderBySql);
    valuesMap.put("frameType", frameType);
    valuesMap.put("frameStart", frameStart);
    valuesMap.put("frameEnd", frameEnd);
    return templateFilling(window_row_frame_001, valuesMap, context);
  }

  @Override
  public String visitFrameBoundary(FrameBoundary node, SqlGeneratorContext context) {
    FrameBoundaryBase base = node.getBase();
    if (base == FrameBoundaryBase.CURRENT_ROW) {
      return "current row";
    }
    if (base == FrameBoundaryBase.UNBOUNDED_FOLLOWING) {
      return "unbounded following";
    }
    if (base == FrameBoundaryBase.UNBOUNDED_PRECEDING) {
      return "unbounded preceding";
    }
    if (base == FrameBoundaryBase.N_FOLLOWING) {
      // offset > 0
      return node.getOffset() + " " + "following";
    }
    if (base == FrameBoundaryBase.N_PRECEDING) {
      return node.getOffset() + " " + "preceding";
    }

    throw new ModelException("if you add new FrameBoundaryBase, you should parse it here");
  }

  @Override
  public String visitModel(Model node, SqlGeneratorContext context) {
    final String ALL_COLUMN = "*";

    // sourceAlias
    Relation source = node.getSource();
    String sourceTableAlias = process(source.getTableName(), context);

    // cteSql
    String cteSql = utilCreateCte(node, context);

    // selectSql
    String selectSql = ALL_COLUMN;
    List<Column> columns = node.getColumns();
    if (columns != null && columns.size() > 0) {
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

    // order by
    String orderBySql = genOrderBySql(node.getOrderBys(), context);
    // limit offset
    String limitSql = node.getLimit() == null ? "" : "limit " + node.getLimit();
    String offsetSql = node.getOffset() == null ? "" : "offset " + node.getOffset();

    // 组装模板
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("cteSql", ResUtils.indent(cteSql));
    valuesMap.put("selectSql", ResUtils.indent(selectSql));
    valuesMap.put("sourceTableAlias", sourceTableAlias);
    valuesMap.put("joinSql", joinSql);
    valuesMap.put("orderBySql", orderBySql);
    valuesMap.put("limitSql", limitSql);
    valuesMap.put("offsetSql", offsetSql);
    return templateFilling(model_001, valuesMap, context);
  }

  @Override
  public String visitNode(AstNode node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }

  @Override
  public String visitColumn(Column node, SqlGeneratorContext context) {
    if (node.getExpr() == null) {
      throw new ModelException("column expression should not be null");
    }

    String alias = node.getAlias();
    String colExpr = process(node.getExpr(), context);
    if (node.getWindow() == null) {
      return "colExpr as alias".replace("colExpr", colExpr).replace("alias", alias);
    }

    // has window
    String windowStr = "\n" + process(node.getWindow(), context);
    return "colExpr window as alias".replace("colExpr", colExpr).replace("window", windowStr)
        .replace("alias", alias);
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
    IdentifierQuotePolicy quotePolicy = context.getQuotePolicy();
    IdentifierShowPolicy showPolicy = context.getShowPolicy();

    String tableName = node.getPrefix();
    String fieldName = node.getValue();

    // no_show_table && no_quote
    // no_show_table && quote
    // show_table && no_quote
    // show_table && quote

    if (showPolicy.showTableName() && tableName != null) {
      if (!quotePolicy.hasQuote()) {
        return tableName + "." + fieldName;
      } else {
        return quotePolicy.quote() + tableName + quotePolicy.quote() + "." + quotePolicy.quote()
            + fieldName + quotePolicy.quote();
      }
    }

    // table_name=null | policy=NO_TABLE_NAME
    if (!quotePolicy.hasQuote()) {
      return fieldName;
    } else {
      return quotePolicy.quote() + fieldName + quotePolicy.quote();
    }
  }

  @Override
  public String visitExpression(Expression node, SqlGeneratorContext context) {
    throw new ModelException("TODO");
  }
}
