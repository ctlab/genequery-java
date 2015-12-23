package com.genequery.bootstrap;

import com.genequery.commons.dao.GeneFrequencyDAO;
import com.genequery.commons.dao.GeneFrequencyFileDAO;
import com.genequery.commons.dao.ModulesDAO;
import com.genequery.commons.dao.ModulesGmtDAO;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.genequery.commons.utils.Utils.checkNotNull;

/**
 * Created by Arbuzov Ivan.
 */
public class Utils {
  @NotNull
  public static DataSet readDataSet() throws IOException {
    System.out.println("Initializing data...");
    Species species = BootstrapProperties.getSpecies();
    String gmtFilename = checkNotNull(BootstrapProperties.getGmtModulesFilename(), "Path to GMT is null");
    ModulesDAO dao = new ModulesGmtDAO(species, Paths.get(gmtFilename));
    DataSet dataSet = new DataSet(species, dao.getAllModules());
    System.out.println(StringUtils.fmt(
      "Data has been initialized: species={}, modules={}.", species, dataSet.getModules().size()
    ));
    return dataSet;
  }

  public static GeneFrequencyDAO getGeneFrequencyDAO() throws IOException {
    String freqToEntrezPath = checkNotNull(BootstrapProperties.getFreqToGenePath(),
      "Path to gene frequency file is null");
    return new GeneFrequencyFileDAO(Paths.get(freqToEntrezPath), "\t");
  }

  @NotNull
  public static int[] getPartition(int[] default_partition) throws IOException {
    String partitionFilename = BootstrapProperties.getRequestLengthsPartitionPath();
    int[] partition;
    if (partitionFilename != null) {
      partition = Files.lines(Paths.get(partitionFilename)).mapToInt(Integer::parseInt).toArray();
    } else {
      System.out.println("Use default request lengths partition.");
      partition = default_partition;
    }
    System.out.println(
      StringUtils.fmt("Request partition length: {}, {}", partition.length, Arrays.toString(partition))
    );
    return partition;
  }

  @NotNull
  public static List<Long> getEntrezIDs() throws IOException {
    int[] stratBounds = BootstrapProperties.getStratBounds();
    if (stratBounds != null) {
      int leftBound = stratBounds[0];
      int rightBound = stratBounds[1];
      if (leftBound > rightBound) {
        throw new RuntimeException("Bad strat bounds: " + leftBound + ":" + rightBound);
      }
      GeneFrequencyDAO dao = getGeneFrequencyDAO();
      Map<Long, Integer> freqs = dao.entrezId2Frequency();

      List<Long> finalEntrezIds = new ArrayList<>();
      for (Long g : dao.getGenesSortedByFreq()) {
        int f = freqs.get(g);
        if (leftBound <= f && f <= rightBound) {
          finalEntrezIds.add(g);
        }
      }
      System.out.println(StringUtils.fmt("Use {} entrez IDs with frequency in [{}, {}]",
        finalEntrezIds.size(), leftBound, rightBound));
      return finalEntrezIds;
    } else {
      String entrezIdsFilename = checkNotNull(BootstrapProperties.getEntrezIdsFilename(), "Path to entrezIDs is null");
      List<Long> entrezIds = Files.lines(Paths.get(entrezIdsFilename)).map(Long::parseLong).collect(Collectors.toList());
      System.out.println("EntrezID total count: " + entrezIds.size());
      return entrezIds;
    }
  }

  public static long[] randomList(int n, List<Long> from, Random random) {
    Set<Long> set = new HashSet<>(n);
    while (set.size() < n) {
      set.add(from.get(random.nextInt(from.size())));
    }
    long[] result = new long[n];
    int i = 0;
    for (long g : set) {
      result[i++] = g;
    }
    return result;
  }
}
