package com.genequery.commons.models;

import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.Collection;
import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
public class DataSet {
  private final Species species;
  private final THashMap<String, Module> modules;
  private final TObjectIntHashMap<String> gse2size;

  public DataSet(Species species, List<Module> modules) {
    this.species = species;
    this.modules = new THashMap<>();
    this.gse2size = new TObjectIntHashMap<>();

    modules.forEach(module -> {
      this.modules.put(module.getName().full(), module);
      gse2size.adjustOrPutValue(module.getName().getGseGpl(), module.getGenes().length, module.getGenes().length);
    });
  }

  public int size() {
    return modules.size();
  }

  public Species getSpecies() {
    return species;
  }

  public Collection<Module> getModules() {
    return modules.values();
  }

  public Module getModuleByFullName(String fullname) {
    return modules.get(fullname);
  }

  public int getGseSize(String gseGplName) {
    return gse2size.get(gseGplName);
  }

  @Override
  public String toString() {
    return getSpecies() + " [" + getModules().size() + " modules]";
  }
}
