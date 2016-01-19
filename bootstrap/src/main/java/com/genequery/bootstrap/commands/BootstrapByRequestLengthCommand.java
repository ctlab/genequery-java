package com.genequery.bootstrap.commands;

import com.genequery.bootstrap.BootstrapOptions;
import com.genequery.bootstrap.Utils;
import com.genequery.commons.dao.GeneFrequencyDAO;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.search.FisherSearcher;
import com.genequery.commons.search.SearchResult;
import com.genequery.commons.utils.StringUtils;
import org.apache.commons.cli.*;
import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * Created by Arbuzov Ivan.
 */
public class BootstrapByRequestLengthCommand extends BaseCommand {

  private Random random = new Random();

  private Callable<List<SearchResult>> search(int requestLength,
                                              List<Long> entrezIds,
                                              DataSet dataSet,
                                              int firstN,
                                              Properties context) {
    return () -> {
      while (true) {
        long[] query = Utils.randomList(requestLength, entrezIds, random);
        Collection<SearchResult> results = FisherSearcher.searchPvalue(dataSet, query, context);
        if (results.isEmpty()) continue;
        return results.stream().sorted().limit(firstN).collect(Collectors.toList());
      }
    };
  }

  @Override
  protected void _execute(CommandLine commandLine) throws IOException {
    String outputFilename = getOutFileName(commandLine);
    boolean withPvalues = commandLine.hasOption(BootstrapOptions.WITH_P_VALUES);
    int[] samplesPerQuerySizes = getFits(commandLine);
    int threadCount = getThreadCount(commandLine);
    System.out.println("Run on " + threadCount + " threads.");

    Species species = getSpecies(commandLine);
    String pathToData = getPathToData(commandLine);

    DataSet dataSet = Utils.readDataSet(species, pathToData);
    int[] partition = Utils.readPartition(commandLine.getOptionValue(BootstrapOptions.PARTITION));

    int[] freqBounds = null;
    GeneFrequencyDAO geneFrequencyDAO = null;
    List<Long> entrezIds;
    if (commandLine.hasOption(BootstrapOptions.FREQUENCY_BOUNDS)) {
      freqBounds = Arrays.stream(commandLine.getOptionValues(BootstrapOptions.FREQUENCY_BOUNDS))
        .mapToInt(Integer::parseInt)
        .toArray();
      geneFrequencyDAO = Utils.readGeneFrequency(species, pathToData);
    }

    Path outputPath = Paths.get(outputFilename);
    System.out.println("Output path " + outputPath);

    int firstN = getFirstN(commandLine);
    System.out.println("Use first " + firstN + " modules for p-value statistic.");

    boolean useTrueGseSize = useTrueGseSize(commandLine);
    System.out.println("Use " + (useTrueGseSize ? "true GSE size" : "6k") + " as gse size in Fisher exact test.");

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      if (withPvalues) {
        writer.write("querySize\tfit\tfreqFrom\tfreqTo\tmeanLogPv\tstdLogPv\tmeanInters\tstdInters\tlogPvalues\n");
      } else {
        writer.write("querySize\tfit\tfreqFrom\tfreqTo\tmeanLogPv\tstdLogPv\tmeanInters\tstdInters\n");
      }
      if (freqBounds == null) {
        entrezIds = Utils.readEntrezIDs(species, pathToData);
        runBootstrapInParallel(withPvalues, samplesPerQuerySizes, dataSet, partition, entrezIds, 1, 100000, firstN,
          useTrueGseSize, executor, writer);
      } else {
        for (int i = 0; i < freqBounds.length - 1; i++) {
          int freqFrom = freqBounds[i];
          int freqTo = freqBounds[i + 1];
          entrezIds = Utils.readEntrezIDsBounded(freqFrom, freqTo, geneFrequencyDAO);
          runBootstrapInParallel(withPvalues, samplesPerQuerySizes, dataSet, partition, entrezIds, freqFrom, freqTo,
            firstN, useTrueGseSize, executor, writer);
        }
      }
    } finally {
      executor.shutdownNow();
    }
  }

  private void runBootstrapInParallel(boolean withPvalues,
                                      int[] samplesPerQuerySizes,
                                      DataSet dataSet,
                                      int[] partition,
                                      List<Long> entrezIds,
                                      int freqFrom, int freqTo,
                                      int firstN, boolean useTrueGseSize,
                                      ExecutorService executor,
                                      BufferedWriter writer) {
    if (entrezIds.isEmpty()) {
      throw new IllegalArgumentException("Result entrez ID list is empty.");
    }

    Properties context = new Properties();
    context.put(FisherSearcher.USE_TRUE_GSE_SIZE, useTrueGseSize);

    for (int queryLength : partition) {
      long t = System.currentTimeMillis();
      for (int samplesPerQuerySize : samplesPerQuerySizes) {
        System.out.print(
          StringUtils.fmt("Running: {} query length, {} fit...", queryLength, samplesPerQuerySize));
        System.out.flush();

        try {
          final List<Callable<List<SearchResult>>> callables = new ArrayList<>();
          for (int i = 0; i < samplesPerQuerySize; ++i) {
            callables.add(search(queryLength, entrezIds, dataSet, firstN, context));
          }

          List<Double> logPvalueStat = new ArrayList<>();
          List<Integer> intersectionSizeStat = new ArrayList<>();

          List<Future<List<SearchResult>>> futures = executor.invokeAll(callables);
          for (Future<List<SearchResult>> future : futures) {
            List<SearchResult> searchResults = future.get();
            for (SearchResult s : searchResults) {
              logPvalueStat.add(s.getLogPvalue());
              intersectionSizeStat.add(s.getIntersectionSize());
            }
          }

          double[] logPvalueStatArr = new double[logPvalueStat.size()];
          double[] intersectionSizeStatArr = new double[intersectionSizeStat.size()];

          for (int i = 0; i < logPvalueStat.size(); i++) {
            logPvalueStatArr[i] = logPvalueStat.get(i);
            intersectionSizeStatArr[i] = intersectionSizeStat.get(i);
          }
          double meanLogPv = StatUtils.mean(logPvalueStatArr);
          double stdLogPv = Math.sqrt(StatUtils.variance(logPvalueStatArr, meanLogPv));
          double meanInters = StatUtils.mean(intersectionSizeStatArr);
          double stdInters = Math.sqrt(StatUtils.variance(intersectionSizeStatArr, meanInters));


          if (withPvalues) {
            String strPvalues = logPvalueStat.stream()
              .map(String::valueOf)
              .collect(Collectors.joining(","));
            writer.write(String.format("%d\t%d\t%d\t%d\t%.16f\t%.16f\t%.16f\t%.16f\t%s\n",
              queryLength, samplesPerQuerySize, freqFrom, freqTo, meanLogPv, stdLogPv, meanInters, stdInters, strPvalues));
          } else {
            writer.write(String.format("%d\t%d\t%d\t%d\t%.16f\t%.16f\t%.16f\t%.16f\n",
              queryLength, samplesPerQuerySize, freqFrom, freqTo, meanLogPv, meanInters, stdInters, stdLogPv));
          }
          writer.flush();

          System.out.println(StringUtils.fmt("Done: time={}ms meanLogPv={} stdLogPv={}", System.currentTimeMillis() - t, meanLogPv, stdLogPv));
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
    System.out.println();
  }
}
