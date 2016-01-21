package com.genequery.rest;

import org.jetbrains.annotations.NotNull;

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
  
  public static String getProperty(@NotNull String key, @NotNull String defaultValue) {
    String property = System.getProperty(key);
    if (property != null) return property;
    property = properties.getProperty(key, defaultValue);
    return property;
  }

  public static int serverPort() {
    return Integer.parseInt(getProperty("genequery.rest.port", "51377"));
  }

  public static double maxEmpiricalPvalue() {
    return Double.parseDouble(getProperty("empirical.p.value.max", "0.01"));
  }

  public static String dbName() {
    return getProperty("db.name", "genequery");
  }

  public static String dbUser() {
    return getProperty("db.user", "smolcoder");
  }

  public static String dbPassword() {
    return getProperty("db.password", "");
  }

  public static String initDataPath() {
    return getProperty("init.data.path", "");
  }

  public static int dbVersion() {
    return Integer.parseInt(getProperty("db.version", "0"));
  }

  public static String[] initSourceOrder() {
    return getProperty("init.source.order", "db,gmt").split(",");
  }
}