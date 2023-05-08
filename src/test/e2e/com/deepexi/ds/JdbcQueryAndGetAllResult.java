package com.deepexi.ds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用jdbc连接 对端数据库, 执行sql. 仅用于测试 sql中需要有 order by, 不然会出错
 */
public class JdbcQueryAndGetAllResult {

  static String JDBC_DRIVER = "org.postgresql.Driver";
  static String DB_URL = "jdbc:postgresql://localhost:5432/tpcds";
  static String USER = "postgres";
  static String PASS = "my-secret-ab";

  public static List<List<Object>> querySelect(String select) {
    Connection conn = null;
    Statement stmt = null;
    try {
      Class.forName(JDBC_DRIVER);
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(select);

      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();
      List<List<Object>> allResult = new ArrayList<>(100);

      while (rs.next()) {
        List<Object> tuple = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
          // String columnName = metaData.getColumnName(i);
          Object value = rs.getObject(i);
          tuple.add(value);
        }
        allResult.add(tuple);
      }

      rs.close();
      stmt.close();
      conn.close();

      return allResult;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException se2) {
        // Do nothing
      }
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    throw new ModelException("execute fail");
  }

  public static void main(String[] args) {
    String sql = "select * from call_center cc";
    List<List<Object>> result = querySelect(sql);
    result.forEach((List<Object> tuple) -> {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < tuple.size(); i++) {
        Object o = tuple.get(i);
        String os = o == null ? "NULL" : o.toString();
        b.append(os).append(", ");
      }
      System.out.println(b);
    });
  }

}
