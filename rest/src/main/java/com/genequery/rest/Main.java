package com.genequery.rest;

import com.genequery.commons.dao.ModulesDAO;
import com.genequery.commons.dao.ModulesGmtDAO;
import com.genequery.commons.dao.ModulesSqlDAO;
import com.genequery.commons.models.Species;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

/**
 * Created by Arbuzov Ivan.
 */
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private static void initDataFromFiles() throws IOException {
    ModulesDAO[] daos = Arrays.asList(Species.HUMAN, Species.MOUSE, Species.RAT)
      .stream()
      .map(species -> {
        Path path = Paths.get(ServerProperties.initDataPath(), species.getText() + ".modules.gmt");
        LOG.info("Loading modules for {}: {}", species, path);
        try {
          return new ModulesGmtDAO(species, path);
        } catch (IOException e) {
          LOG.warn("Can't load modules for " + species, e);
          return null;
        }
      })
      .filter(dao -> dao != null)
      .toArray(ModulesDAO[]::new);

    if (daos.length == 0) {
      throw new IOException("No data loaded from files.");
    }
    DataSetHolder.init(daos);
  }


  private static void initDataFromDB() throws Exception {
    Class.forName("org.postgresql.Driver");
    LOG.info("Using DB " + ServerProperties.dbName() + ", version " + ServerProperties.dbVersion());
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql:" + ServerProperties.dbName(),
      ServerProperties.dbUser(),
      ServerProperties.dbPassword()
    )) {
      DataSetHolder.init(new ModulesSqlDAO(connection));
    }
  }

  private static void initData() {
    String[] initSourceOrder = ServerProperties.initSourceOrder();
    LOG.info("Init source order: {}", Arrays.toString(initSourceOrder));

    boolean loaded = false;
    for (String source : initSourceOrder) {
      try {
        LOG.info("Initializing data from {}...", source.toUpperCase());
        if ("db".equals(source)) {
          initDataFromDB();
          loaded = true;
          break;
        } else if ("gmt".equals(source)) {
          initDataFromFiles();
          loaded = true;
          break;
        } else {
          LOG.warn("Unknown data loading source: {}. Skipping it.", source);
        }
      } catch (Exception e) {
        LOG.warn("Error while loading data from " + source, e);
      }
    }
    if (loaded) {
      LOG.info("Data has been initialized. hs: {}, mm: {}, rt: {} modules.",
        DataSetHolder.getDataSet(Species.HUMAN).size(),
        DataSetHolder.getDataSet(Species.MOUSE).size(),
        DataSetHolder.getDataSet(Species.RAT).size());
    } else {
      LOG.error("Data hasn't been loaded. Stopping.");
      System.exit(1);
    }
  }

  private static void initProperties() throws IOException {
    String propertiesPath = System.getProperty("server.properties");
    if (propertiesPath == null) {
      ServerProperties.initDefault();
    } else {
      ServerProperties.init(propertiesPath);
    }
  }

  public static void main(String[] args) {
    try {
      initProperties();
    } catch (IOException e) {
      LOG.error("Properties initialization error.", e);
      System.exit(1);
    }

    initData();

    RestServer server = new RestServer();
    try {
      server.start(ServerProperties.serverPort());
    } catch (Exception e) {
      LOG.error("Error while initializing REST server.", e);
      System.exit(1);
    }
  }
}
