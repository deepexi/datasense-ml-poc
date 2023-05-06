package com.deepexi.ds.builder;

import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ColumnNotExistException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
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
import com.deepexi.ds.builder.express.BoolConditionParser;
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
import lombok.Data;
import lombok.Getter;

@SuppressWarnings("unchecked")
@Getter
public class ModelBuilder {

  @Data
  private static class Container {

    private Identifier name;
    private Relation source;
    private List<Join> joins;
    private List<Column> columns;
    private List<Column> dimensions;
    private List<Relation> scopes = new ArrayList<>();

    Model build() {
      return new Model(name, source, joins, columns, dimensions);
    }

    void addRelation(Relation r, boolean isSource) {
      scopes.add(r);
      if (isSource) {
        source = r;
      }
    }
  }

  private final List<YmlModel> models;
  private final Map<String, YmlModel> lookup;
  private final YmlModel entry;

  public static Model singleTreeModel(List<YmlModel> models) {
    ModelBuilder builder = new ModelBuilderFactory(models).create();
    return builder.build();
  }

  public ModelBuilder(List<YmlModel> models, YmlModel entry) {
    this.models = models;
    this.entry = entry;
    lookup = models.stream().collect(Collectors.toMap(YmlModel::getName, Function.identity()));
  }

  public Model build() {
    Container ctx = new Container();
    buildRoot(entry, ctx);
    return ctx.build();
  }

  private void buildRoot(YmlModel root, Container ctx) {
    // name => identifier
    ctx.setName(Identifier.of(root.getName()));
    // source
    parseSource(root.getSource(), ctx);
    Relation srcRel = ctx.getSource();
    // joins
    parseJoins(srcRel, root.getJoins(), ctx);
    // columns
    parseColumn(srcRel, root.getColumns(), ctx);
    // dimensions
    parseDimension(srcRel, root.getDimensions(), ctx);
  }

  private void parseSource(YmlSource source, Container ctx) {
    Objects.requireNonNull(source, "source must be present");

    if (source instanceof YmlSourceTable) {
      YmlSourceTable src = (YmlSourceTable) source;
      TableSource t = new TableSource(src.getDataSource(), Identifier.of(src.getTableName()));
      ctx.addRelation(t, true);
    } else if (source instanceof YmlSourceModel) {
      YmlSourceModel src = (YmlSourceModel) source;
      String modelName = src.getModelName();
      YmlModel ymlModel = requireModel(modelName);
      Container subCtx = new Container();
      buildRoot(ymlModel, subCtx);
      Model subModel = subCtx.build();
      ctx.addRelation(subModel, true);
    }
  }

  private void parseJoins(Relation srcRel, List<YmlJoin> joins, Container ctx) {
    if (joins == null || joins.isEmpty()) {
      ctx.setJoins(EMPTY_LIST);
      return;
    }

    List<Join> joinList = new ArrayList<>(joins.size());
    for (YmlJoin join : joins) {
      String modelName = join.getModelName();
      YmlModel ymlModel = requireModel(modelName);
      Container subCtx = new Container();
      buildRoot(ymlModel, subCtx);
      Model subModel = subCtx.build();
      ctx.addRelation(subModel, false);

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
          // 目前 literal中是一个 原子条件
          // 多个条件之间是 Logic.AND 运算
          Expression expr = new BoolConditionParser(literal, ctx.getScopes(), srcRel).parse();
          expressions.add(expr);
        }
      }
      // done
      joinList.add(new Join(subModel, joinType, expressions));
    }
    ctx.setJoins(joinList);
  }

  private void parseColumn(Relation srcRel, List<YmlColumn> list, Container ctx) {
    if (list == null || list.isEmpty()) {
      ctx.setColumns(EMPTY_LIST);
      return;
    }

    List<Column> columns = new ArrayList<>(list.size());
    for (YmlColumn col : list) {
      Column column = parseColumn(col, srcRel, ctx);
      columns.add(column);
    }
    ctx.setColumns(columns);
  }

  private Column parseColumn(YmlColumn col, Relation srcRel, Container ctx) {
    Expression expression = ParserUtils.parseStandaloneExpression(col.getExpr());
    String colAlias = col.getName();
    ColumnDataType type1 = ColumnDataType.fromName(col.getDataType());

    if (expression instanceof Identifier) {
      Identifier colId = (Identifier) expression;
      // 这个 column 由其他列 直接得来. eg
      // tableA.colX as alias
      // 该字段类型, 该字段所引用的列 的类型
      // 如果有一个, 则使用
      // 如果不同, 则需要 类型转换
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

      if (type1 != null && referColDataType != null && type1 != referColDataType) {
        // 隐式cast
        UdfCastExpression udfCast = new UdfCastExpression(Arrays.asList(
            new Identifier(fromTable.getTableName().getValue(), referColumn.getAlias()),
            new DataTypeLiteral(type1.name)
        ));
        return new Column(colAlias, udfCast, type1);
      }

      if (type1 == null) {
        type1 = referColDataType;
      }
      return new Column(colAlias, colId, type1);
    }

    if (expression instanceof Literal) {
      // 这个列是个固定值. eg
      // 100 as alias
      // TODO 根据 Literal子类型 推到 data_type
      throw new ModelException("目前暂不支持 固定值 列");
    }

    // 其他情况, 这个列是派生计算而来
    Column colRaw = new Column(colAlias, expression, type1);
    Column colWithTable = (Column) new ColumnTableNameAdder(srcRel).process(colRaw);
    if (expression instanceof FunctionExpression) {
      // 如果是函数, 需要额外处理一下
      return (Column) new ColumnInFunctionHandler(ctx.getScopes()).process(colWithTable);
    }
    return colWithTable;
  }

  private void parseDimension(Relation srcRel, List<YmlDimension> list, Container ctx) {
    if (list == null || list.isEmpty()) {
      ctx.setDimensions(EMPTY_LIST);
      return;
    }
    List<Column> dims = new ArrayList<>(list.size());

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
      Column dim = new Column(refCol.getAlias(), expr, refCol.getDataType());
      dims.add(dim);
    }
    ctx.setDimensions(dims);
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
