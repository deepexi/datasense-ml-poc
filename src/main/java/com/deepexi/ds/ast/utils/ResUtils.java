package com.deepexi.ds.ast.utils;

import com.deepexi.ds.DevConfig;
import com.deepexi.ds.ModelException;
import com.deepexi.ds.SqlDialect;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResUtils {

  private static final String DEFAULT_DIR = "default";
  /**
   * sql文件路径模板
   */
  private static final String SQL_FILE_PATTERN = "sql/%s/%s.sql";
  private static final String PATTERN_DEBUG = "-- res/sql/%s/%s.sql\n";
  /**
   * all lines start with --! will be removed
   */
  private static Predicate<String> NOT_COMMENT = (String line) -> !line.startsWith("--!");


  private static String getResourceFileAsString(String fileName, String debugInfo)
      throws ModelException {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
      if (is == null) {
        return null;
      }
      try (InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr)) {
        String template = reader.lines()
            .filter(NOT_COMMENT)
            .collect(Collectors.joining(System.lineSeparator()));
        if (DevConfig.DEBUG) {
          template = debugInfo + template;
        }
        return template;
      }
    } catch (IOException e) {
      throw new ModelException(e);
    }
  }

  public static String getSqlTemplate(SqlTemplateId templateId, SqlDialect dialect) {
    String path = String.format(SQL_FILE_PATTERN, dialect.name.toLowerCase(), templateId.fileName);
    try {
      String debugInfo = String.format(PATTERN_DEBUG, dialect.name, templateId.fileName);
      String sql = getResourceFileAsString(path, debugInfo);
      if (sql != null) {
        return sql;
      }
    } catch (Exception ex) {
      // not sql for this dialect
      System.out.printf(
          "should provide sql for dialect=[%s], sql_template=[%s]%n",
          dialect.name,
          templateId.fileName);
    }

    // if not got, find the default dialect
    String debugInfo = String.format(PATTERN_DEBUG, DEFAULT_DIR, templateId.fileName);
    String path4Default = String.format(SQL_FILE_PATTERN, DEFAULT_DIR, templateId.fileName);
    return getResourceFileAsString(path4Default, debugInfo);
  }

  // 检测sql中是否有占位符 ${.*?}
  public static boolean noPlaceHolder(String sql) {
    return !(sql.matches("\\$\\{.*?}"));
  }

  /**
   * 添加缩进 基本原则: 如果模板中 某个变量块 有缩进, 则 需要给这个加缩进. 如下的模板
   * <pre>
   * ${alias} as (
   *     ${querySql}
   * )
   * </pre>
   * alias 不缩进, querySql 缩进
   * <p>
   * 如果一个模板整体缩进, 则不需要添加, 如下几个变量无需缩进
   * <pre>
   *    over (
   *         ${partitionSql}
   *         ${orderBySql}
   *         ${frameType} between ${frameStart} and ${frameEnd}
   *     )
   * </pre>
   */
  public static String indent(String in) {
    return in.replaceAll("\n", "\n    ");
  }
}
