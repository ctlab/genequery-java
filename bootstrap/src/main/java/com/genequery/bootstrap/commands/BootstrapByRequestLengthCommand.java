package com.genequery.bootstrap.commands;

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

  private Callable<SearchResult> search(int requestLength,
                                            List<Long> entrezIds,
                                            DataSet dataSet) {
    return () -> {
      while (true) {
        long[] query = Utils.randomList(requestLength, entrezIds, random);
        Collection<SearchResult> results = FisherSearcher.searchPvalue(dataSet, query);
        if (results.isEmpty()) continue;
        return Collections.min(results);
      }
    };
  }

  @Override
  protected void _execute(CommandLine commandLine) throws IOException {
    String outputFilename = getOutFileName(commandLine);
    boolean withPvalues = commandLine.hasOption(BootstrapOptions.WITH_P_VALUES);
    int[] samplesPerQuerySizes = getFits(commandLine);
    int threadCount = getThreadCount(commandLine);

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
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      if (withPvalues) {
        writer.write("querySize\tfit\tfreqFrom\tfreqTo\tmean\tstd\tminLogPvalues\n");
      } else {
        writer.write("querySize\tfit\tfreqFrom\tfreqTo\tmean\tstd\n");
      }
      if (freqBounds == null) {
        entrezIds = Utils.readEntrezIDs(species, pathToData);
        runBootstrapInParallel(withPvalues, samplesPerQuerySizes, dataSet, partition, entrezIds, 1, 100000, executor, writer);
      } else {
        for (int i = 0; i < freqBounds.length - 1; i++) {
          int freqFrom = freqBounds[i];
          int freqTo = freqBounds[i + 1];
          entrezIds = Utils.readEntrezIDsBounded(freqFrom, freqTo, geneFrequencyDAO);
          runBootstrapInParallel(withPvalues, samplesPerQuerySizes, dataSet, partition, entrezIds, freqFrom, freqTo, executor, writer);
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
                                      ExecutorService executor,
                                      BufferedWriter writer) {
    if (entrezIds.isEmpty()) {
      throw new IllegalArgumentException("Result entrez ID list is empty.");
    }
    for (int queryLength : partition) {
      long t = System.currentTimeMillis();
      for (int samplesPerQuerySize : samplesPerQuerySizes) {
        System.out.print(
          StringUtils.fmt("Running: {} query length, {} fit...", queryLength, samplesPerQuerySize));
        System.out.flush();

        try {
          final List<Callable<SearchResult>> callables = new ArrayList<>();
          for (int i = 0; i < samplesPerQuerySize; ++i) {
            callables.add(search(queryLength, entrezIds, dataSet));
          }

          double[] statistics = new double[samplesPerQuerySize];

          List<Future<SearchResult>> futures = executor.invokeAll(callables);
          for (int i = 0; i < futures.size(); ++i) {
            SearchResult s = futures.get(i).get();
            statistics[i] = s.getLogPvalue();
          }

          double mean = StatUtils.mean(statistics);
          double std = Math.sqrt(StatUtils.variance(statistics, mean));
          if (withPvalues) {
            String strPvalues = Arrays.stream(statistics)
              .mapToObj(String::valueOf)
              .collect(Collectors.joining(","));
            writer.write(String.format("%d\t%d\t%d\t%d\t%.16f\t%.16f\t%s\n",
              queryLength, samplesPerQuerySize, freqFrom, freqTo, mean, std, strPvalues));
          } else {
            writer.write(String.format("%d\t%d\t%d\t%d\t%.16f\t%.16f\n",
              queryLength, samplesPerQuerySize, freqFrom, freqTo, mean, std));
          }
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
    System.out.println();
  }

  @Override
  protected Options getCustomOptions() {
    Options options = new Options();

    options.addOption(
      OptionBuilder
        .withArgName("PATH")
        .withLongOpt("partition")
        .hasArgs(1)
        .isRequired(true)
        .withDescription("Path to partition file.")
        .create(BootstrapOptions.PARTITION)
    );

    options.addOption(
      OptionBuilder
        .withLongOpt("with-p-values")
        .hasArg(false)
        .isRequired(false)
        .withDescription("Print p-values to output.")
        .create(BootstrapOptions.WITH_P_VALUES)
    );

    options.addOption(
      OptionBuilder
        .withLongOpt("freq-bounds")
        .withArgName("bound_1,...,bound_n")
        .hasArgs()
        .withValueSeparator(',')
        .isRequired(false)
        .withDescription("Use only genes which frequency lay in [low, high]. Must be comma-separated.")
        .create(BootstrapOptions.FREQUENCY_BOUNDS)
    );

    return options;
  }
}
