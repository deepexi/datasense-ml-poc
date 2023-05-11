package com.deepexi.ds.astbuilder.model;

import com.deepexi.ds.DevConfig;
import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ColumnNotExistException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.DateTimeUnit;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.JoinType;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.DataTypeLiteral;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.FunctionExpression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.expression.Literal;
import com.deepexi.ds.ast.expression.UdfCastExpression;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.builder.express.BaseColumnIdentifierChecker;
import com.deepexi.ds.builder.express.ColumnInFunctionHandler;
import com.deepexi.ds.builder.express.ColumnTableNameAdder;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlColumn;
import com.deepexi.ds.ymlmodel.YmlDimension;
import com.deepexi.ds.ymlmodel.YmlJoin;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.YmlSource;
import com.deepexi.ds.ymlmodel.YmlSourceModel;
import com.deepexi.ds.ymlmodel.YmlSourceTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@SuppressWarnings("unchecked")
@Getter
public class ModelBuilder {

  private final List<YmlModel> models;          // all models
  private final Map<String, YmlModel> lookup;   // modelName--YmlModel
  private final YmlModel entry;                 // root of the models

  /**
   * internal usage
   */
  ModelBuilder(List<YmlModel> models, YmlModel entry) {
    this.models = models;
    this.entry = entry;
    lookup = models.stream().collect(Collectors.toMap(YmlModel::getName, Function.identity()));
  }

  public static Model singleTreeModel(List<YmlModel> models) {
    ModelBuilder builder = new ModelBuilderFactory(models).create();
    return builder.build();
  }

  public Model build() {
    ModelPartCollector collector = new ModelPartCollector();
    buildRoot(entry, collector);
    Model notRewrite = collector.build();
    Model rewrite = (Model) new ColumnTableNameAdder(notRewrite.getSource()).process(notRewrite);
    new BaseColumnIdentifierChecker(rewrite).process(rewrite);
    return rewrite;
  }

  private void buildRoot(YmlModel root, ModelPartCollector collector) {
    collector.setName(Identifier.of(root.getName())); // name => identifier
    // 先序处理 source / join
    parseSource(root.getSource(), collector);         // source
    parseJoins(root.getJoins(), collector);           // joins
    parseColumn(root.getColumns(), collector);        // columns
    parseDimension(root.getDimensions(), collector);  // dimensions
  }

  private void parseSource(YmlSource source, ModelPartCollector collector) {
    Objects.requireNonNull(source, "source must be present");

    if (source instanceof YmlSourceTable) {
      // YmlSourceTable 已经是外部资源, 无需在分析下去
      YmlSourceTable src = (YmlSourceTable) source;
      TableSource t = new TableSource(src.getDataSource(), Identifier.of(src.getTableName()));
      collector.addSource(t);
    } else if (source instanceof YmlSourceModel) {
      YmlSourceModel src = (YmlSourceModel) source;
      String modelName = src.getModelName();
      YmlModel ymlModel = requireModel(modelName);
      ModelPartCollector subCollector = new ModelPartCollector();
      buildRoot(ymlModel, subCollector);
      Model subModel = subCollector.build();
      collector.addSource(subModel);
    }
  }

  private void parseJoins(List<YmlJoin> joins, ModelPartCollector ctx) {
    if (joins == null || joins.isEmpty()) {
      return;
    }

    for (YmlJoin join : joins) {
      String modelName = join.getModelName();
      YmlModel ymlModel = requireModel(modelName);
      ModelPartCollector subCtx = new ModelPartCollector();
      buildRoot(ymlModel, subCtx);
      Model subModel = subCtx.build();
      ctx.addScope(subModel);

      // join type
      JoinType joinType = JoinType.fromName(join.getJoinType());
      if (joinType == null) {
        joinType = JoinType.INNER;
      }

      // condition
      List<String> conditions = join.getConditions();
      List<Expression> expressions = new ArrayList<>();
      if (conditions != null && conditions.size() > 0) {
        for (String literal : conditions) {
          // 目前 literal中是一个 原子条件, 多个条件之间是 Logic.AND 运算
          Expression expr = ParserUtils.parseBooleanExpression(literal);
          expressions.add(expr);
        }
      }
      ctx.addJoin(new Join(subModel, joinType, expressions));
    }
  }

  private void parseColumn(List<YmlColumn> list, ModelPartCollector ctx) {
    if (list == null || list.isEmpty()) {
      return;
    }
    for (YmlColumn col : list) {
      Column column = parseColumnOfAllCase(col, ctx.getSource(), ctx);
      ctx.addColumn(column);
    }
  }

  private Column parseColumnOfAllCase(YmlColumn col, Relation srcRel, ModelPartCollector ctx) {
    Expression expression = ParserUtils.parseStandaloneExpression(col.getExpr());
    String colAlias = col.getName();
    ColumnDataType dataType = ColumnDataType.fromName(col.getDataType());
    DateTimeUnit dateTimeUnit = DateTimeUnit.fromName(col.getDatePart());

    if (expression instanceof Identifier) {
      // 这个 column 由其他列 直接得来. eg
      // tableA.colX as alias
      // 该字段类型, 该字段所引用的列 的类型
      // 如果有一个, 则使用
      // 如果不同, 则需要 类型转换
      Identifier colId = (Identifier) expression;
      Relation fromTable = srcRel;
      if (colId.getPrefix() != null) {
        fromTable = assertTableInScope(colId.getPrefix(), ctx.getScopes());
      } else {
        colId = new Identifier(srcRel.getTableName().getValue(), colId.getValue());
      }

      Column referColumn = assertColumnExistsInRelation(colId.getValue(), fromTable);
      ColumnDataType referColDataType = null;
      if (referColumn != null) {
        referColDataType = referColumn.getDataType();
      }
      if (referColumn != null && dateTimeUnit == null) {
        dateTimeUnit = referColumn.getDatePart();
      }

      boolean needCast = dataType != null
          && referColDataType != null
          && dataType != referColDataType;
      if (needCast) {
        UdfCastExpression udfCast = new UdfCastExpression(Arrays.asList(
            new Identifier(fromTable.getTableName().getValue(), referColumn.getAlias()),
            new DataTypeLiteral(dataType.name)
        ));
        if (dataType == ColumnDataType.DATE) {
          dateTimeUnit = DateTimeUnit.DATE;
        } else if (dataType == ColumnDataType.DATETIME) {
          dateTimeUnit = DateTimeUnit.DATETIME;
        } else if (dataType == ColumnDataType.TIMESTAMP) {
          dateTimeUnit = DateTimeUnit.TIMESTAMP;
        }
        return new Column(colAlias, udfCast, dataType, dateTimeUnit, null);
      }

      if (dataType == null) {
        dataType = referColDataType;
      }
      if (dataType == null) {
        if (DevConfig.DEBUG) {
          System.out.println(String.format("column %s data type is null", colAlias));
        }
      }
      return new Column(colAlias, colId, dataType, dateTimeUnit, null);
    }

    if (expression instanceof Literal) {
      // 这个列是个固定值. eg
      // 100 as alias
      // TODO 根据 Literal子类型 推到 data_type
      throw new ModelException("目前暂不支持 固定值 列");
    }

    // 其他情况, 这个列是派生计算而来
    if (dataType == null) {
      if (DevConfig.DEBUG) {
        System.out.println(String.format("column %s data type is null", colAlias));
      }
    }
    Column colRaw = new Column(colAlias, expression, dataType, dateTimeUnit, null);
    Column colWithTable = (Column) new ColumnTableNameAdder(srcRel).process(colRaw);
    if (expression instanceof FunctionExpression) {
      // 如果是函数, 需要额外处理一下
      return (Column) new ColumnInFunctionHandler(ctx.getScopes()).process(colWithTable);
    }
    return colWithTable;
  }

  private void parseDimension(List<YmlDimension> list, ModelPartCollector ctx) {
    if (list == null || list.isEmpty()) {
      return;
    }

    for (YmlDimension col : list) {
      String name = col.getName();
      Column refCol = ctx.getColumns().stream()
          .filter(column -> column.getAlias().equals(name))
          .findAny().orElse(null);
      if (refCol == null) {
        throw new ModelException(String.format("dimension [%s] not exists in columns", name));
      }
      // 重新组装 dimension, 比如 原来引用 tableA.colA => {currentTable}.colA
      Identifier expr = new Identifier(ctx.getName().getValue(), refCol.getAlias());
      Column dim = new Column(refCol.getAlias(), expr, refCol.getDataType(), refCol.getDatePart(),
          null);
      ctx.addDimension(dim);
    }
  }

  private YmlModel requireModel(String name) {
    YmlModel ymlModel = lookup.get(name);
    if (ymlModel == null) {
      throw new ModelNotFoundException(name);
    }
    return ymlModel;
  }

  public static Relation assertTableInScope(String tableName, List<Relation> scope) {
    Optional<Relation> anyRelation = scope.stream()
        .filter(t -> Objects.equals(t.getTableName().getValue(), tableName))
        .findAny();
    if (!anyRelation.isPresent()) {
      throw new ModelException(String.format(
          "table [%s] not accessible, either not define or illegal reference", tableName));
    }
    return anyRelation.get();
  }

  public static Column assertColumnExistsInRelation(String colName, Relation rel) {
    if (rel == null) {
      return null;
    }
    Column column = rel.getColumn(colName);
    if (column == null) {
      throw new ColumnNotExistException(rel.getTableName().getValue(), colName);
    }
    return column;
  }
}
