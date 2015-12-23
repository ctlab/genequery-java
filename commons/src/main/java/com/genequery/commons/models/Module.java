package com.genequery.commons.models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

  @NotNull
  public List<Long> getIntersection(List<Long> other) {
    long[] array = new long[other.size()];
    for (int i = 0; i < other.size(); i++) {
      array[i] = other.get(i);
    }
    return getIntersection(array);
  }

  @NotNull
  public List<Long> getIntersection(long[] other) {
    long[] a = genes;
    long[] b = other;
    List<Long> intersection = new ArrayList<>();

    if (a.length > b.length) {
      long[] t = a;
      a = b;
      b = t;
    }

    int ia = 0, ib = 0;

    while (ia < a.length && ib < b.length) {
      if (a[ia] == b[ib]) {
        intersection.add(a[ia]);
        ia++;
        ib++;
      } else if (a[ia] > b[ib]) {
        ib++;
      } else {
        ia++;
      }
    }
    return intersection;
  }
}
