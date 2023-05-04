package com.deepexi.ds.builder;

import static com.deepexi.ds.ast.expression.Identifier.RE_IDENTIFIER_SEPARATOR;
import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ColumnNotExistException;
import com.deepexi.ds.ModelException.FieldMissException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.JoinType;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.Relation;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.builder.express.BoolConditionParser;
import com.deepexi.ds.builder.express.ColumnTableNameRewriter;
import com.deepexi.ds.parser.ParserUtils;
import com.deepexi.ds.ymlmodel.YmlColumn;
import com.deepexi.ds.ymlmodel.YmlDimension;
import com.deepexi.ds.ymlmodel.YmlJoin;
import com.deepexi.ds.ymlmodel.YmlModel;
import com.deepexi.ds.ymlmodel.YmlSource;
import com.deepexi.ds.ymlmodel.YmlSourceModel;
import com.deepexi.ds.ymlmodel.YmlSourceTable;
import java.util.ArrayList;
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
    private List<RelationMock> scopes = new ArrayList<>();
    private RelationMock sourceRelation;

    Model build() {
      return new Model(name, source, joins, columns, dimensions);
    }

    void addRelationMock(RelationMock r, boolean isSource) {
      scopes.add(r);
      if (isSource) {
        sourceRelation = r;
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
    RelationMock srcRel = ctx.getSourceRelation();
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
      // this source is external
      YmlSourceTable src = (YmlSourceTable) source;
      TableSource t = new TableSource(src.getDataSource(), Identifier.of(src.getTableName()));
      ctx.setSource(t);
      ctx.addRelationMock(RelationMock.fromTableSource(t), true);
    } else if (source instanceof YmlSourceModel) {
      YmlSourceModel src = (YmlSourceModel) source;
      String modelName = src.getModelName();
      YmlModel ymlModel = requireModel(modelName);
      Container subCtx = new Container();
      buildRoot(ymlModel, subCtx);
      Model subModel = subCtx.build();
      ModelSource modelSource = new ModelSource(subModel);
      ctx.setSource(modelSource);
      ctx.addRelationMock(RelationMock.fromMode(subModel), true);
    }
  }

  private void parseJoins(RelationMock srcRel, List<YmlJoin> joins, Container ctx) {
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
      RelationMock relationMock = RelationMock.fromMode(subModel);
      ctx.addRelationMock(relationMock, false);

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

  private void parseColumn(RelationMock srcRel, List<YmlColumn> list, Container ctx) {
    if (list == null || list.isEmpty()) {
      ctx.setColumns(EMPTY_LIST);
      return;
    }

    List<Column> columns = new ArrayList<>(list.size());
    for (YmlColumn col : list) {
      if (col.isBasic()) {
        Column column = parseBasicColumn(col, srcRel, ctx);
        columns.add(column);
      } else {
        Column column = parseDerivedColumn(col, srcRel, ctx);
        columns.add(column);
      }
    }
    ctx.setColumns(columns);
  }

  private Column parseBasicColumn(YmlColumn col, RelationMock srcRel, Container ctx) {
    String colName = col.getName();
    String srcTableName = srcRel.getTableName().getValue();
    // case: colName or table.colName
    String expr = col.getExpr();
    if (expr == null) {
      expr = colName;
    }
    String[] fields = expr.split(RE_IDENTIFIER_SEPARATOR);
    Identifier fromCol;
    Column referColumn;
    if (fields.length == 1) {
      fromCol = new Identifier(srcTableName, fields[0].trim());
      referColumn = assertColumnExistsInRelation(fields[0].trim(), srcRel);
    } else if (fields.length == 2) {
      String targetTable = fields[0].trim();
      // targetTable should exists, column should in targetTable table
      RelationMock relation = assertTableInScope(targetTable, ctx.getScopes());
      referColumn = assertColumnExistsInRelation(fields[1].trim(), relation);
      fromCol = new Identifier(fields[0].trim(), fields[1].trim());
    } else {
      throw new ModelException("expr not support too many dot");
    }

    // type: 可以由上下文推到得到
    String type = col.getDataType();
    ColumnDataType type1 = ColumnDataType.fromName(type);
    if (type1 == null) {
      type1 = referColumn.getDataType();
    }
    if (type1 == null) {
      throw new FieldMissException("data_type of " + colName);
    }
    // done
    return new Column(colName, fromCol, type1/*, col.getExpr()*/);
  }

  private Column parseDerivedColumn(YmlColumn col, RelationMock srcRel, Container ctx) {
    String colName = col.getName();
    String type = col.getDataType();
    ColumnDataType type1 = ColumnDataType.fromName(type); // dataType必须有
    if (type1 == null) {
      throw new FieldMissException("data_type of " + colName);
    }
    Expression expression = ParserUtils.parseStandaloneExpression(col.getExpr());
    Column colRaw = new Column(colName, expression, type1);
    return (Column) new ColumnTableNameRewriter(srcRel.getTableName()).process(colRaw);
  }

  private void parseDimension(RelationMock srcRel, List<YmlDimension> list, Container ctx) {
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
      Identifier refExpr = (Identifier) refCol.getExpr(); // TODO maybe 出问题
      // 重新组装 dimension, 比如 原来引用 tableA.colA => {currentTable}.colA
      Identifier expr = new Identifier(ctx.getName().getValue(), refExpr.getValue());
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

  public static RelationMock assertTableInScope(String tableName, List<RelationMock> scope) {
    Optional<RelationMock> anyRelation = scope.stream()
        .filter(t -> Objects.equals(t.getTableName().getValue(), tableName))
        .findAny();
    if (!anyRelation.isPresent()) {
      throw new ModelException(String.format(
          "table [%s] not accessible, either not define or illegal reference", tableName));
    }
    return anyRelation.get();
  }

  public static Column assertColumnExistsInRelation(String colName, RelationMock rel) {
    Column column = rel.getColumn(colName);
    if (column == null) {
      throw new ColumnNotExistException(rel.getTableName().getValue(), colName);
    }
    return column;
  }
}
