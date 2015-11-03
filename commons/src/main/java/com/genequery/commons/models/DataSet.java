package com.genequery.commons.models;

import gnu.trove.map.hash.THashMap;

import java.util.Collection;
import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
public class DataSet {
    private final Species species;
    private final THashMap<String, Module> modules;

    public DataSet(Species species, List<Module> modules) {
        this.species = species;
        this.modules = new THashMap<>();

        modules.forEach(module -> this.modules.put(module.getName().full(), module));
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

    @Override
    public String toString() {
        return getSpecies() + " [" + getModules().size() + " modules]";
    }
}
