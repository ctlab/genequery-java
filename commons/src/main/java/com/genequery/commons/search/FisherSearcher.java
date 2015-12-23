package com.genequery.commons.search;

import com.genequery.commons.math.FisherExactTest;
import com.genequery.commons.math.FisherTable;
import com.genequery.commons.math.Normal;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherSearcher {

  /**
   * TODO
   * @param dataSet
   * @param query
   * @param empPvalueThreshold
   * @return
   */
  public static List<SearchResult> search(DataSet dataSet, long[] query, double empPvalueThreshold) {
    List<SearchResult> result = searchPvalue(dataSet, Arrays.copyOf(query, query.length));
    return result.stream()
        .map(searchResult -> {
          searchResult.setEmpiricalPvalue(
              calculateEmpiricalPvalue(dataSet.getSpecies(), query.length, searchResult.getLogPvalue())
          );
          return searchResult;
        })
        .filter(searchResult -> searchResult.getEmpiricalPvalue() <= empPvalueThreshold)
        .collect(Collectors.toList());
  }

  /**
   * TODO
   * @param dataSet
   * @param query
   * @return
   */
  public static List<SearchResult> searchPvalue(DataSet dataSet, long[] query) {
    Arrays.sort(query);
    Collection<Module> modules = dataSet.getModules();
    final TObjectIntHashMap<String> overlaps = new TObjectIntHashMap<>(modules.size());
    final TObjectIntHashMap<String> overlapsWithGSE = new TObjectIntHashMap<>();

    populateOverlaps(modules, query, overlaps, overlapsWithGSE);

    final List<SearchResult> result = new ArrayList<>(overlaps.size());
    modules.forEach(module -> {
      int seriesOverlap = overlapsWithGSE.get(module.getName().getGseGpl());
      int a = overlaps.get(module.getName().full());
      int b = module.getGenes().length - a;
      int c = seriesOverlap - a;
      int d = 6000 - seriesOverlap - module.getGenes().length + a;

      if (a == 0) return;

      FisherTable table = new FisherTable(a, b, c, d);
      double pvalue = FisherExactTest.rightTail(table);

      result.add(new SearchResult(module, pvalue, a, table, module.getIntersection(query)));
    });
    return result;
  }

  private static void populateOverlaps(Collection<Module> modules,
                                       long[] sortedQuery,
                                       TObjectIntHashMap<String> overlaps,
                                       TObjectIntHashMap<String> overlapsWithGSE) {
    modules.forEach(
        module -> {
          int intersectionLength = module.getIntersection(sortedQuery).size();
          if (intersectionLength == 0) return;
          overlaps.put(module.getName().full(), intersectionLength);
          overlapsWithGSE.adjustOrPutValue(module.getName().getGseGpl(), intersectionLength, intersectionLength);
        }
    );
  }

  private static double calculateEmpiricalPvalue(Species species, int moduleSize, double logPvalue) {
    return Normal.cdf(logPvalue, getMean(species, moduleSize), getStd(species, moduleSize));
  }

  private static double getStd(Species species, int moduleSize) {
    if (species == Species.MOUSE) return 0.982519d + 0.000769d * moduleSize;
    if (species == Species.HUMAN) return 1.027662d + 0.000939d * moduleSize;
    throw new IllegalArgumentException("No empirical mean for species " + species);
  }

  private static double getMean(Species species, int moduleSize) {
    if (species == Species.MOUSE) return -3.06942d - 0.01322d * moduleSize;
    if (species == Species.HUMAN) return -2.2151d - 0.0187d * moduleSize;
    throw new IllegalArgumentException("No empirical std for species " + species);
  }
}
