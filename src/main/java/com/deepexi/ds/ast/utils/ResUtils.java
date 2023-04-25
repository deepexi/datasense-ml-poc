package com.deepexi.ds.ast.utils;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ast.SqlDialect;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResUtils {

  public static Predicate<String> NOT_COMMENT = line -> !line.startsWith("--!");

  public static String getResourceFileAsString(String fileName) throws ModelException {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
      if (is == null) {
        return null;
      }
      try (InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().filter(NOT_COMMENT)
            .collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (IOException e) {
      throw new ModelException(e);
    }
  }

  /**
   * 如果必要 可以 cache, 因为是常量, 不会变动
   */
  private static final String SQL_FILE_PATTERN = "sql/%s/%s.sql";

  public static String getSqlTemplate(SqlTemplateId templateId, SqlDialect dialect) {
    String path4Dialect = String.format(SQL_FILE_PATTERN, dialect.name.toLowerCase(),
        templateId.fileName);
    String path4Default = String.format(SQL_FILE_PATTERN, "default", templateId.fileName);
    try {
      String sql = getResourceFileAsString(path4Dialect);
      if (sql != null) {
        return sql;
      }
    } catch (Exception ex) {
      // not sql for this dialect
      System.out.println(
          String.format("should provide sql for dialect=[%s], sql_template=[%s]", dialect.name,
              templateId.fileName));
    }
    return getResourceFileAsString(path4Default);
  }
}
