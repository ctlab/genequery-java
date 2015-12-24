package com.genequery.bootstrap;

import com.genequery.commons.dao.ModulesDAO;
import com.genequery.commons.dao.ModulesGmtDAO;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.search.FisherSearcher;
import com.genequery.commons.search.SearchResult;
import com.genequery.commons.utils.StringUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
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

  private static double LOG_2 = Math.log(2);

  private static class Statistic {
    public double logMinPvalue;
    public List<Long> intersection;

    public Statistic(double logMinPvalue, List<Long> intersection) {
      this.logMinPvalue = logMinPvalue;
      this.intersection = intersection;
    }
  }

  private static Random random = new Random();

//  private static int[] DEFAULT_REQUEST_LENGTHS = {
//      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
//      20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100,
//      110, 120, 130, 140, 150, 160, 170, 180, 190, 200,
//      225, 250, 275, 300, 325, 350, 375, 400,
//      450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000,
//      1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000,
//      3250, 3500, 3750, 4000, 4250, 4500, 4750, 5000,
//  };

  private static int[] DEFAULT_REQUEST_LENGTHS = {
    5, 10, 15, 20, 30, 40, 50, 75, 100, 125, 150, 200, 250, 300, 350, 400, 500,
  };




  private static Callable<Statistic> search(int requestLength,
                                            List<Long> entrezIds,
                                            DataSet dataSet) {
    return () -> {
      while (true) {
        long[] query = Utils.randomList(requestLength, entrezIds, random);
        Collection<SearchResult> results = FisherSearcher.searchPvalue(dataSet, query);
        if (results.isEmpty()) continue;
        SearchResult min = Collections.min(results);
        return new Statistic(min.getLogPvalue(), min.getIntersection());
      }
    };
  }

  public static void main(String[] args) throws Exception {
    System.out.println(StringUtils.fmt("Using properties: {}", BootstrapProperties.asProperties()));
    if (args.length == 0) {
      throw new IllegalArgumentException("Bootstrapping kind is not specified.");
    }
    if (args[0].equals("byquery")) {
      byQuerySizeBootstrap();
    } else if (args[0].equals("overall")) {
      byOverallSizeBootstrap();
    } else if (args[0].equals("emulate")) {

    } else {
      throw new IllegalArgumentException("Unknown key: " + args[0]);
    }
  }

  private static int randInt(int min, int max) {
    return random.nextInt((max - min) + 1) + min;
  }

  private static void byOverallSizeBootstrap() throws IOException, InterruptedException {
    String outputFilename = BootstrapProperties.getOutputPath();
    int threadCount = BootstrapProperties.getThreadCount();
    int overallRequestCount = BootstrapProperties.getOverallRequestCount();
    int queryLenFrom = BootstrapProperties.getQueryLenFrom();
    int queryLenTo = BootstrapProperties.getQueryLenTo();
//    GeneFrequencyDAO geneFrequencyDAO = Utils.getGeneFrequencyDAO();

    System.out.println("Overall request count: " + overallRequestCount);
    System.out.println(StringUtils.fmt("Using request length range: from {} to {}.", queryLenFrom, queryLenTo));

    DataSet dataSet = Utils.readDataSet();
    List<Long> entrezIds = Utils.getEntrezIDs();
    if (entrezIds.isEmpty()) {
      throw new IllegalArgumentException("Result entrez ID list is empty.");
    }

    System.out.println("Let's go!");

    final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {
      long t = System.currentTimeMillis();
      final List<Callable<Statistic>> callables = new ArrayList<>();
      final List<Integer> requestLengths = new ArrayList<>();

      for (int overallRequestIterator = 0; overallRequestIterator < overallRequestCount; ++overallRequestIterator) {
        requestLengths.add(randInt(queryLenFrom, queryLenTo));
      }

      Collections.sort(requestLengths);

      callables.addAll(
        requestLengths
          .stream()
          .map(requestLength -> search(requestLength, entrezIds, dataSet))
          .collect(Collectors.toList())
      );
      List<Future<Statistic>> futures = callables.stream().map(executor::submit).collect(Collectors.toList());
      for (int i = 0; i < callables.size(); ++i) {
        System.out.print("\r" + (i + 1) + " done...");
        Future<Statistic> statisticFuture = futures.get(i);
        int requestLength = requestLengths.get(i);
        try {
          double minPvalue = statisticFuture.get().logMinPvalue;
          writer.write(String.format("%d\t%.16f\n", requestLength, minPvalue));
          writer.flush();
        } catch (ExecutionException e) {
          System.err.println("Exception while calculating future.");
          e.printStackTrace();
        }
      }
      System.out.println();
      System.out.println(StringUtils.fmt("Done: time={}ms", System.currentTimeMillis() - t));
    }
    executor.shutdownNow();
  }

  private static void byQuerySizeBootstrap() throws IOException {
//    String outputFilename = BootstrapProperties.getOutputPath();
//    boolean withPvalues = BootstrapProperties.getWithPvalues();
//    int[] samplesPerQuerySizes = BootstrapProperties.getSamplesPerQuerySizes();
//    int threadCount = BootstrapProperties.getThreadCount();
//
//    DataSet dataSet = Utils.readDataSet();
//    int[] partition = Utils.getPartition(DEFAULT_REQUEST_LENGTHS);
//    List<Long> entrezIds = Utils.getEntrezIDs();
//    if (entrezIds.isEmpty()) {
//      throw new IllegalArgumentException("Result entrez ID list is empty.");
//    }
//
//    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//
//    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {
//      long t = System.currentTimeMillis();
//      for (int requestLength : partition) {
//        for (int samplesPerQuerySize : samplesPerQuerySizes) {
//          System.out.print(
//            StringUtils.fmt("Running for {} request length and {} sample fit...", requestLength, samplesPerQuerySize));
//          System.out.flush();
//
//          try {
//            final List<Callable<Statistic>> callables = new ArrayList<>();
//            for (int i = 0; i < samplesPerQuerySize; ++i) {
//              callables.add(search(requestLength, entrezIds, dataSet));
//            }
//
//            double[] statistics = new double[samplesPerQuerySize];
//
//            List<Future<Statistic>> futures = executor.invokeAll(callables);
//            for (int i = 0; i < futures.size(); ++i) {
//              Statistic s = futures.get(i).get();
//              statistics[i] = s.logMinPvalue;
//            }
//
//            double mean = StatUtils.mean(statistics);
//            double std = Math.sqrt(StatUtils.variance(statistics, mean));
//            if (withPvalues) {
//              String strPvalues = Arrays.stream(statistics)
//                .mapToObj(String::valueOf)
//                .collect(Collectors.joining(","));
//              writer.write(String.format("%d\t%d\t%.16f\t%.16f\t%s\n",
//                requestLength, samplesPerQuerySize, mean, std, strPvalues));
//            } else {
//              writer.write(String.format("%d\t%d\t%.16f\t%.16f\n",
//                requestLength, samplesPerQuerySize, mean, std));
//            }
//            writer.flush();
//
//            System.out.println(StringUtils.fmt("Done: time={}ms mean={} std={}", System.currentTimeMillis() - t, mean, std));
//            t = System.currentTimeMillis();
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//            break;
//          } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Continue.");
//          }
//        }
//      }
//    }
//    executor.shutdownNow();
  }


}