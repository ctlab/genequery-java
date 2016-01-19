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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.genequery.commons.utils.Utils.checkNotNull;

/**
 * Created by Arbuzov Ivan.
 */
public class Utils {

  @NotNull
  public static DataSet readDataSet(Species species, String pathToData) throws IOException {
    Path pathToGmt = Paths.get(pathToData, StringUtils.fmt("{}.modules.gmt", species.getText()));
    System.out.println("Initializing data set from " + pathToGmt);
    ModulesDAO dao = new ModulesGmtDAO(species, pathToGmt);
    DataSet dataSet = new DataSet(species, dao.getAllModules());
    System.out.println(StringUtils.fmt(
      "Data has been initialized: species={}, modules={}.", species, dataSet.getModules().size()
    ));
    return dataSet;
  }

  public static GeneFrequencyDAO readGeneFrequency(Species species, String pathToData) throws IOException {
    Path pathToFreq = Paths.get(pathToData, StringUtils.fmt("{}.freq.entrez.txt", species.getText()));
    System.out.println("Initializing frequency DAO from " + pathToFreq);
    return new GeneFrequencyFileDAO(pathToFreq, "\t");
  }

  @NotNull
  public static int[] readPartition(String partitionFile) throws IOException {
    Path pathToPartition = Paths.get(partitionFile);
    int[] partition;
    partition = Files.lines(pathToPartition).mapToInt(Integer::parseInt).toArray();
    System.out.println(
      StringUtils.fmt("Request partition length: {}, {}", partition.length, Arrays.toString(partition))
    );
    return partition;
  }

  @NotNull
  public static List<Long> readEntrezIDsBounded(int freqFrom,
                                                int freqTo,
                                                GeneFrequencyDAO frequencyDAO) throws IOException {
    if (freqFrom > freqTo) {
      throw new RuntimeException("Bad strat bounds: " + freqFrom + ":" + freqTo);
    }
    Map<Long, Integer> freqs = frequencyDAO.entrezId2Frequency();

    List<Long> finalEntrezIds = new ArrayList<>();
    for (Long g : frequencyDAO.getGenesSortedByFreq()) {
      int f = freqs.get(g);
      if (freqFrom <= f && f <= freqTo) {
        finalEntrezIds.add(g);
      }
    }
    System.out.println(StringUtils.fmt("Use {} entrez IDs with frequency in [{}, {}]",
      finalEntrezIds.size(), freqFrom, freqTo));
    return finalEntrezIds;
  }

  @NotNull
  public static List<Long> readEntrezIDs(Species species, String pathToData) throws IOException {
    Path pathToEntrez = Paths.get(pathToData, StringUtils.fmt("{}.entrez.txt", species.getText()));
    List<Long> entrezIds = Files.lines(pathToEntrez).map(Long::parseLong).collect(Collectors.toList());
    System.out.println("EntrezID total count: " + entrezIds.size());
    return entrezIds;
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
