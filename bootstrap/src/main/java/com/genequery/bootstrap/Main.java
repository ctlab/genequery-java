package com.genequery.bootstrap;

import com.genequery.commons.dao.ModulesDAO;
import com.genequery.commons.dao.ModulesGmtDAO;
import com.genequery.commons.math.FisherExactTest;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.search.FisherSearcher;
import com.genequery.commons.search.SearchResult;
import com.genequery.commons.utils.StringUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.genequery.commons.utils.Utils.checkNotNull;

/**
 * Created by Arbuzov Ivan.
 */
public class Main {

  private static Random random = new Random();

  // TODO make input argument
  private static int[] REQUEST_LENGTHS = {
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
      20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100,
      110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
      225, 250, 275, 300, 325, 350, 375, 400,
      450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000,
      1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000,
      3250, 3500, 3750, 4000, 4250, 4500, 5000,
  };


  private static long[] randomList(int n, List<Long> from) {
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

  private static Callable<Double> search(int requestLength,
                                         List<Long> entrezIds,
                                         DataSet dataSet) {
    return () -> {
      double pvalue = Double.MAX_VALUE;
      while (pvalue == Double.MAX_VALUE) {
        long[] query = randomList(requestLength, entrezIds);
        Collection<SearchResult> results = FisherSearcher.searchPvalue(dataSet, query);
        if (results.isEmpty()) continue;
        pvalue = Collections.min(results).getLogPvalue();
      }
      return pvalue;
    };
  }

  public static void main(String[] args) throws Exception {
    System.out.println(StringUtils.fmt("Using properties: {}", BootstrapProperties.asProperties()));

    String gmtFilename = checkNotNull(BootstrapProperties.getGmtModulesFilename(), "Path to GMT is null");
    String entrezIdsFilename = checkNotNull(BootstrapProperties.getEntrezIdsFilename(), "Path to entrezIDs is null");
    Species species = BootstrapProperties.getSpecies();
    String outputFilename = BootstrapProperties.getOutputFilename();

    int samplesPerQuerySize = BootstrapProperties.getSamplesPerQuerySize();
    int threadCount = BootstrapProperties.getThreadCount();

    System.out.println("Initializing data...");
    ModulesDAO dao = new ModulesGmtDAO(species, gmtFilename);
    DataSet dataSet = new DataSet(species, dao.getAllModules());
    System.out.println(StringUtils.fmt(
        "Data has been initialized: species={}, modules={}.", species, dataSet.getModules().size()
    ));

    List<Long> entrezIds = Files.lines(Paths.get(entrezIdsFilename)).map(Long::parseLong).collect(Collectors.toList());
    System.out.println("EntrezID total count: " + entrezIds.size());


    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {
      long t = System.currentTimeMillis();
      for (int requestLength : REQUEST_LENGTHS) {
        System.out.print("Running for " + requestLength + " request length... ");
        try {
          final List<Callable<Double>> callables = new ArrayList<>();
          for (int i = 0; i < samplesPerQuerySize; ++i) {
            callables.add(search(requestLength, entrezIds, dataSet));
          }

          double[] pvalues = new double[samplesPerQuerySize];

          List<Future<Double>> futures = executor.invokeAll(callables);
          for (int i = 0; i < futures.size(); ++i) {
            pvalues[i] = futures.get(i).get();
          }

          double mean = StatUtils.mean(pvalues);
          double std = Math.sqrt(StatUtils.variance(pvalues, mean));
          writer.write(String.format("%d\t%.16f\t%.16f\n", requestLength, mean, std));
          writer.flush();

          System.out.println(StringUtils.fmt("Done: time={}ms mean={} std={}", System.currentTimeMillis() - t, mean, std));
          t = System.currentTimeMillis();
        } catch (InterruptedException e) {
          e.printStackTrace();
          break;
        } catch (Exception e) {
          e.printStackTrace();
          System.err.println("Continue.");
        }
      }
    }
    executor.shutdownNow();

  }
}
