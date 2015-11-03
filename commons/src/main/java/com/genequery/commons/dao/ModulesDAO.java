package com.genequery.commons.dao;

import com.genequery.commons.models.Module;

import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
public interface ModulesDAO {
  List<Module> getAllModules() throws Exception;
}
