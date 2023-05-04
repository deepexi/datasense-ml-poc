package com.deepexi.ds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 使用jdbc连接 对端数据库, 执行sql.
 * 仅用于测试, 且目前仅比对 row count
 * 目前连接 pg, 如果连接其他, 需要在 mvn中配置 jdbc驱动
 */
public class JdbcUtils {

  static String JDBC_DRIVER = "org.postgresql.Driver";
  static String DB_URL = "jdbc:postgresql://localhost:5432/tpcds";
  static String USER = "postgres";
  static String PASS = "my-secret-ab";

  public static int queryCount(String select) {
    String wrapSql = String.format("select count(*) _count_ from (%s) _table_", select);
    Connection conn = null;
    Statement stmt = null;
    try {
      Class.forName(JDBC_DRIVER);
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(wrapSql);

      int count = 0;
      while (rs.next()) {
        count = rs.getInt("_count_");
        break;
      }

      rs.close();
      stmt.close();
      conn.close();

      return count;
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
    System.out.println(queryCount(sql));
  }
}
