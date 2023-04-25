package com.deepexi.ds.ast.utils;

import com.deepexi.ds.ModelException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ResUtils {

  public static String getResourceFileAsString(String fileName) throws ModelException {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
      if (is == null) {
        return null;
      }
      try (InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (IOException e) {
      throw new ModelException(e);
    }
  }
}
