package com.genequery.commons.dao;

import com.genequery.commons.models.Module;

import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
public interface ModulesDAO {
  /**
   * Return all modules extracted by this DAO.
   * It may be both for all species at once and for just single species.
   *
   * @return list of modules contained in the DAO.
   */
  List<Module> getAllModules();
}
