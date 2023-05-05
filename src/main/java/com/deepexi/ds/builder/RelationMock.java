//package com.deepexi.ds.builder;
//
//import com.deepexi.ds.ast.Column;
//import com.deepexi.ds.ast.Model;
//import com.deepexi.ds.ast.expression.Identifier;
//import com.deepexi.ds.ast.expression.StringLiteral;
//import com.deepexi.ds.ast.source.TableSource;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.Getter;
//
///**
// * 记录一张表基础信息= 表名 + 字段, 用于临时分析
// */
//@Getter
//public class RelationMock {
//
//  private final Identifier tableName;
//  private final List<Column> columns;
//  private final boolean hasAnyColumn;
//
//  public RelationMock(String tableName, List<String> colNames) {
//    this.tableName = Identifier.of(tableName);
//    this.columns = colNames.stream()
//        .map(name -> new Column(name, StringLiteral.of("*"), null))
//        .collect(Collectors.toList());
//    this.hasAnyColumn = false;
//  }
//
//  public RelationMock(Identifier tableName, List<Column> columns) {
//    this.tableName = tableName;
//    this.columns = columns;
//    this.hasAnyColumn = false;
//  }
//
//  public RelationMock(Identifier tableName, List<Column> columns, boolean hasAnyColumn) {
//    this.tableName = tableName;
//    this.columns = columns;
//    this.hasAnyColumn = hasAnyColumn;
//  }
//
//  public Column getColumn(String colName) {
//    if (hasAnyColumn) {
//      return Column.ALL_COLUMN;
//    }
//    if (columns == null || columns.size() == 0) {
//      return null;
//    }
//    return columns.stream()
//        .filter(column -> column.getAlias().equals(colName))
//        .findAny().orElse(null);
//  }
//
//  public static RelationMock fromMode(Model node) {
//    return new RelationMock(node.getName(), node.getColumns());
//  }
//
//  public static RelationMock fromTableSource(TableSource t) {
//    return new RelationMock(t.getTableName(), t.getColumns(), true);
//  }
//}
