package com.genequery.commons.dao;

import com.genequery.commons.models.Module;
import com.genequery.commons.models.ModuleName;
import com.genequery.commons.models.Species;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Arbuzov Ivan.
 */
public class ModulesSqlDAO implements ModulesDAO {

  private final Connection connection;

  public ModulesSqlDAO(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<Module> getAllModules() throws Exception {
    try (Statement statement = connection.createStatement()) {
      ResultSet rs = statement.executeQuery("SELECT * FROM module_genes");
      List<Module> modules = new ArrayList<>();

      while (rs.next()) {
        Integer[] genes = (Integer[]) rs.getArray("entrez_ids").getArray();
        Species species = Species.fromString(rs.getString("species"));
        String fullName = rs.getString("module");
        long[] resGenes = new long[genes.length];
        // Entrez IDs are supposed to be sorted in descending order
        IntStream.rangeClosed(0, genes.length - 1).forEach(i -> resGenes[genes.length - i - 1] = genes[i]);
        modules.add(new Module(new ModuleName(fullName), species, resGenes));
      }
      return modules;
    }
  }
}
