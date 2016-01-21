package com.genequery.commons.search;

import com.genequery.commons.math.FisherExactTest;
import com.genequery.commons.math.FisherTable;
import com.genequery.commons.math.Normal;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;
import gnu.trove.map.hash.TObjectIntHashMap;

import javax.naming.Context;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherSearcher {

  public static String USE_TRUE_GSE_SIZE = "use.true.gse.size";
  public static String DB_VERSION = "db.version";

  /**
   * TODO
   * @param dataSet
   * @param query
   * @param empPvalueThreshold
   * @return
   */
  public static List<SearchResult> search(DataSet dataSet, long[] query, double empPvalueThreshold,
                                          Properties context) {
    List<SearchResult> result = searchPvalue(dataSet, Arrays.copyOf(query, query.length), context);
    final int dbVersion  = (int)context.getOrDefault(DB_VERSION, -1);
    return result.stream()
        .map(searchResult -> {
          searchResult.setEmpiricalPvalue(
              calculateEmpiricalPvalue(dataSet.getSpecies(), query.length, searchResult.getLogPvalue(), dbVersion)
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
  public static List<SearchResult> searchPvalue(DataSet dataSet, long[] query, Properties context) {
    Arrays.sort(query);
    Collection<Module> modules = dataSet.getModules();
    final TObjectIntHashMap<String> overlaps = new TObjectIntHashMap<>(modules.size());
    final TObjectIntHashMap<String> overlapsWithGSE = new TObjectIntHashMap<>();

    populateOverlaps(modules, query, overlaps, overlapsWithGSE);

    final List<SearchResult> result = new ArrayList<>(overlaps.size());
    final boolean useRealGseSize = (boolean)context.getOrDefault(USE_TRUE_GSE_SIZE, false);
    modules.forEach(module -> {
      int seriesOverlap = overlapsWithGSE.get(module.getName().getGseGpl());
      int gseSize = useRealGseSize ? dataSet.getGseSize(module.getName().getGseGpl()) : 6000;
      int a = overlaps.get(module.getName().full());
      int b = module.getGenes().length - a;
      int c = seriesOverlap - a;
      int d = gseSize - seriesOverlap - module.getGenes().length + a;

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

  private static double calculateEmpiricalPvalue(Species species, int moduleSize, double logPvalue, int dbVersion) {
    return Normal.cdf(logPvalue, getMean(species, moduleSize, dbVersion), getStd(species, moduleSize, dbVersion));
  }

  private static double getStd(Species species, int moduleSize, int dbVersion) {
    if (dbVersion == DataSet.DB_2013) {
      switch (species) {
        case HUMAN: return 1.128394343e-6 * moduleSize + 0.5539313336;
        case MOUSE: return 2.505819324e-6 * moduleSize + 0.5472805136;
      }
    } else if (dbVersion == DataSet.DB_2015) {
      switch (species) {
        case HUMAN: return -4.909174514e-6 * moduleSize + 0.5612293253;
        case MOUSE: return 9.370360256e-6 * moduleSize + 0.5418300854;
        case RAT: return -1.667001492e-5 * moduleSize + 0.5512707705;
      }
    }
    throw new IllegalArgumentException("No empirical std for species " + species + " and DB version " + dbVersion);
  }


  private static double getMean(Species species, int moduleSize, int dbVersion) {
    if (dbVersion == DataSet.DB_2013) {
      switch (species) {
        case HUMAN: return -0.08455403386 * Math.log(moduleSize) - 4.269244644;
        case MOUSE: return -0.0869592962 * Math.log(moduleSize) - 4.090598767;
      }
    } else if (dbVersion == DataSet.DB_2015) {
      switch (species) {
        case HUMAN: return -0.08347915069 * Math.log(moduleSize) - 4.443941868;
        case MOUSE: return -0.08848028394 * Math.log(moduleSize) - 4.269100238;
        case RAT: return -0.08350190746 * Math.log(moduleSize) - 3.568849077;
      }
    }
    throw new IllegalArgumentException("No empirical mean for species " + species + " and DB version " + dbVersion);
  }
}
