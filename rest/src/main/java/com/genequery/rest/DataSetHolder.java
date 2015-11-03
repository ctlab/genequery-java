package com.genequery.rest;

import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;
import com.genequery.rest.dao.GQModuleDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arbuzov Ivan.
 */
public class DataSetHolder {
  private static final Map<Species, DataSet> species2dataset = new HashMap<>();

  private DataSetHolder() {
  }

  public static void init(Connection connection) throws SQLException {
    GQModuleDAO dao = new GQModuleDAO(connection);

    List<Module> modules = dao.getAllModules();

    final List<Module> hsModules = new ArrayList<>();
    final List<Module> mmModules = new ArrayList<>();
    final List<Module> rtModules = new ArrayList<>();

    modules.forEach(module -> {
      if (module.getSpecies() == Species.MOUSE) {
        mmModules.add(module);
      }
      if (module.getSpecies() == Species.HUMAN) {
        hsModules.add(module);
      }
      if (module.getSpecies() == Species.RAT) {
        rtModules.add(module);
      }
    });
    species2dataset.put(Species.HUMAN, new DataSet(Species.HUMAN, hsModules));
    species2dataset.put(Species.MOUSE, new DataSet(Species.MOUSE, mmModules));
    species2dataset.put(Species.RAT, new DataSet(Species.RAT, rtModules));
  }

  public static DataSet getDataSet(Species species) {
    return species2dataset.get(species);
  }
}
