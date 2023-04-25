package com.deepexi.ds.builder;

import static com.deepexi.ds.ast.expression.Identifier.RE_IDENTIFIER_SEPARATOR;
import static java.util.Collections.EMPTY_LIST;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.ColumnNotExistException;
import com.deepexi.ds.ModelException.ModelNotFoundException;
import com.deepexi.ds.ast.Column;
import com.deepexi.ds.ast.ColumnDataType;
import com.deepexi.ds.ast.Dimension;
import com.deepexi.ds.ast.Join;
import com.deepexi.ds.ast.JoinType;
import com.deepexi.ds.ast.Model;
import com.deepexi.ds.ast.expression.Expression;
import com.deepexi.ds.ast.expression.Identifier;
import com.deepexi.ds.ast.source.ModelSource;
import com.deepexi.ds.ast.source.Source;
import com.deepexi.ds.ast.source.TableSource;
import com.deepexi.ds.yml2pojo.YmlColumn;
import com.deepexi.ds.yml2pojo.YmlDimension;
import com.deepexi.ds.yml2pojo.YmlJoin;
import com.deepexi.ds.yml2pojo.YmlModel;
import com.deepexi.ds.yml2pojo.YmlSource;
import com.deepexi.ds.yml2pojo.YmlSourceModel;
import com.deepexi.ds.yml2pojo.YmlSourceTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;

@Getter
public class AstModelBuilder {

  @Data
  private static class Container {

    private Identifier name;
    private Source source;
    private List<Join> joins;
    private List<Column> columns;
    private List<Dimension> dimensions;
    private List<RelationMock> scopes = new ArrayList<RelationMock>();
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

  public static AstModelBuilder singleTreeModel(List<YmlModel> models) {
    return new AstModelBuilderFactory(models).create();
  }

  AstModelBuilder(List<YmlModel> models, YmlModel entry) {
    this.models = models;
    this.entry = entry;
    lookup = models.stream().collect(Collectors.toMap(YmlModel::getName, Function.identity()));
  }

  public Model build() {
    Container ctx = new Container();
    buildRoot(entry, ctx);

    Model build = ctx.build();
    return build;
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
    for (int i = 0; i < joins.size(); i++) {
      YmlJoin join = joins.get(i);
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
        for (int j = 0; j < conditions.size(); j++) {
          // 目前 literal中是一个 原子条件
          // 多个条件之间是 Logic.AND 运算
          String literal = conditions.get(j);
          Expression expr = new ExpressionParser(literal, ctx.getScopes(), srcRel).parse();
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
    String srcTableName = srcRel.getTableName().getValue();

    for (YmlColumn col : list) {
      String name = col.getName();

      // case: colName or table.colName
      String expr = col.getExpr();
      if (expr == null) {
        expr = name;
      }
      String[] fields = expr.split(RE_IDENTIFIER_SEPARATOR);
      Identifier fromCol = null;
      Column referColumn = null;
      if (fields.length == 1) {
        fromCol = new Identifier(srcTableName, fields[0].trim());
        referColumn = assertColumnExistsInRelation(fields[0].trim(), srcRel);
      } else if (fields.length == 2) {
        String targetTable = fields[0].trim();
        // targetTable should exists, column should in targetTable table
        RelationMock relation = assertTableExists(targetTable, ctx.getScopes());
        referColumn = assertColumnExistsInRelation(fields[1].trim(), relation);
        fromCol = new Identifier(fields[0].trim(), fields[1].trim());
      } else {
        throw new ModelException("expr not support too many dot");
      }

      // type: 可以由上下文推到得到
      String type = col.getType();
      ColumnDataType type1 = ColumnDataType.fromName(type);
      if (type1 == null && referColumn != null) {
        type1 = referColumn.getType();
      }
      if (type1 == null) {
        throw new ModelException(String.format("cannot defer type for column [%s]", name));
      }
      // done
      columns.add(new Column(name, fromCol, type1, col.getExpr()));
    }
    ctx.setColumns(columns);
  }

  private void parseDimension(RelationMock srcRel, List<YmlDimension> list, Container ctx) {
    if (list == null || list.isEmpty()) {
      ctx.setDimensions(EMPTY_LIST);
      return;
    }
    List<Dimension> dims = new ArrayList<>(list.size());
    String srcTableName = srcRel.getTableName().getValue();

    for (YmlDimension col : list) {
      String name = col.getName();

      // case: colName or table.colName
      String expr = col.getExpr();
      if (expr == null) {
        expr = name;
      }
      String[] fields = expr.split("\\.");
      Identifier fromCol = null;
      if (fields.length == 1) {
        fromCol = new Identifier(srcTableName, fields[0].trim());
      } else if (fields.length == 2) {
        String targetTable = fields[0].trim();
        // targetTable should exists, column should in targetTable table
        RelationMock relation = assertTableExists(targetTable, ctx.getScopes());
        fromCol = new Identifier(fields[0].trim(), fields[1].trim());
      } else {
        throw new ModelException("expr not support too many dot");
      }

      // done
      dims.add(new Dimension(name, fromCol, col.getExpr()));
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

  public static RelationMock assertTableExists(String tableName, List<RelationMock> scope) {
    Optional<RelationMock> anyRelation = scope.stream()
        .filter(t -> Objects.equals(t.getTableName().getValue(), tableName))
        .findAny();
    if (anyRelation.isEmpty()) {
      throw new ModelNotFoundException(tableName);
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
