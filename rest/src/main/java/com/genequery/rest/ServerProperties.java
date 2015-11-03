package com.genequery.rest;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Arbuzov Ivan.
 */
public class ServerProperties {
  private static final String DEFAULT_PROPERTIES_PATH = "/default.properties";

  private final static Properties properties = new Properties();

  public static void initDefault() throws IOException {
    try (InputStream stream = ServerProperties.class.getResourceAsStream(DEFAULT_PROPERTIES_PATH)) {
      properties.load(stream);
    }
  }

  public static void init(String path) throws IOException {
    properties.load(new FileReader(path));
  }

  public static int serverPort() {
    return Integer.parseInt(properties.getProperty("genequery.rest.port", "51377"));
  }

  public static double maxEmpiricalPvalue() {
    return Double.parseDouble(properties.getProperty("empirical.p.value.max", "0.01"));
  }

  public static String dbName() {
    return properties.getProperty("db.name", "genequery");
  }

  public static String dbUser() {
    return properties.getProperty("db.user", "smolcoder");
  }

  public static String dbPassword() {
    return properties.getProperty("db.password", "");
  }
}