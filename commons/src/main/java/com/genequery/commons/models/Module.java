package com.genequery.commons.models;

import java.util.Arrays;

/**
 * Created by Arbuzov Ivan.
 */
public class Module {
  private final ModuleName name;
  private final Species species;
  private final long[] genes;


  /**
   * @param name name
   * @param species species
   * @param genes <i>sorted</i> list of genes
   */
  public Module(ModuleName name, Species species, long[] genes) {
    this.name = name;
    this.species = species;
    this.genes = genes;
  }

  public ModuleName getName() {
    return name;
  }

  public long[] getGenes() {
    return genes;
  }

  public Species getSpecies() {
    return species;
  }

  public int intersectionSize(long[] other) {
    long[] a = genes;
    long[] b = other;

    if (a.length > b.length) {
      long[] t = a;
      a = b;
      b = t;
    }

    int ia = 0, ib = 0;
    int result = 0;

    while (ia < a.length && ib < b.length) {
      if (a[ia] == b[ib]) {
        result++;
        ia++;
        ib++;
      } else if (a[ia] > b[ib]) {
        ib++;
      } else {
        ia++;
      }
    }
    return result;
  }
}
