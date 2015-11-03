package com.genequery.rest.dao;

import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Arbuzov Ivan.
 */
public class GQModuleDAO {
    private final Connection connection;

    public GQModuleDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Module> getAllModules() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM module_genes LIMIT 10");
            List<Module> modules = new ArrayList<>();

            while (rs.next()) {
                Integer[] genes = (Integer[])rs.getArray("entrez_ids").getArray();
                Species species = Species.fromString(rs.getString("species"));
                String fullName = rs.getString("module");
                long[] resGenes = new long[genes.length];
                IntStream.rangeClosed(0, genes.length - 1).forEach(i -> resGenes[genes.length - i - 1] = genes[i]);
                modules.add(new Module(fullName, species, resGenes));
            }
            return modules;
        }
    }
}
