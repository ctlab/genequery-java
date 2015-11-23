package com.genequery.rest;

import com.genequery.commons.dao.ModulesSqlDAO;
import com.genequery.commons.models.Species;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Arbuzov Ivan.
 */
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private static void initData() throws Exception {
    Class.forName("org.postgresql.Driver");
    try (Connection connection = DriverManager.getConnection(
        "jdbc:postgresql:" + ServerProperties.dbName(),
        ServerProperties.dbUser(),
        ServerProperties.dbPassword()
    )) {
      LOG.info("Initializing data...");
      DataSetHolder.init(new ModulesSqlDAO(connection));
      LOG.info("Data has been initialized. hs: {}, mm: {}, rt: {} modules.",
          DataSetHolder.getDataSet(Species.HUMAN).size(),
          DataSetHolder.getDataSet(Species.MOUSE).size(),
          DataSetHolder.getDataSet(Species.RAT).size());
    }
  }

  public static void main(String[] args) {

    String propertiesPath = System.getProperty("server.properties");
    try {
      if (propertiesPath == null) {
        ServerProperties.initDefault();
      } else {
        ServerProperties.init(propertiesPath);
      }
    } catch (IOException e) {
      LOG.error("Properties initialization error.", e);
      System.exit(1);
    }

    try {
      initData();
    } catch (Exception e) {
      LOG.error("Error while initializing data.", e);
      System.exit(1);
    }

    RestServer server = new RestServer();
    try {
      server.start(ServerProperties.serverPort());
    } catch (Exception e) {
      LOG.error("Error while initializing REST server.", e);
      System.exit(1);
    }
  }
}
