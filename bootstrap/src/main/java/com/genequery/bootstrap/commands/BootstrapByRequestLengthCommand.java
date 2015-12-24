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
    boolean withPvalues = commandLine.hasOption("wpv");
    int[] samplesPerQuerySizes = getFits(commandLine);
    int threadCount = getThreadCount(commandLine);

    Species species = getSpecies(commandLine);
    String pathToData = getPathToData(commandLine);

    DataSet dataSet = Utils.readDataSet(species, pathToData);
    int[] partition = Utils.readPartition(commandLine.getOptionValue("p"));

    int[] freqBounds;
    List<Long> entrezIds;
    if (commandLine.hasOption("b")) {
      freqBounds = Arrays.stream(commandLine.getOptionValues("b")).mapToInt(Integer::parseInt).toArray();
      GeneFrequencyDAO geneFrequencyDAO = Utils.readGeneFrequency(species, pathToData);
      entrezIds = Utils.readEntrezIDsBounded(freqBounds[0], freqBounds[1], geneFrequencyDAO);
    } else {
      entrezIds = Utils.readEntrezIDs(species, pathToData);
    }
    if (entrezIds.isEmpty()) {
      throw new IllegalArgumentException("Result entrez ID list is empty.");
    }

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {
      long t = System.currentTimeMillis();
      for (int queryLength : partition) {
        for (int samplesPerQuerySize : samplesPerQuerySizes) {
          System.out.print(
            StringUtils.fmt("Running for {} request length and {} sample fit...", queryLength, samplesPerQuerySize));
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
              writer.write(String.format("%d\t%d\t%.16f\t%.16f\t%s\n",
                queryLength, samplesPerQuerySize, mean, std, strPvalues));
            } else {
              writer.write(String.format("%d\t%d\t%.16f\t%.16f\n",
                queryLength, samplesPerQuerySize, mean, std));
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
    }
    executor.shutdownNow();
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
        .create("p")
    );

    options.addOption(
      OptionBuilder
        .withLongOpt("with-p-values")
        .hasArg(false)
        .isRequired(false)
        .withDescription("Print p-values to output.")
        .create("wpv")
    );

    options.addOption(
      OptionBuilder
        .withLongOpt("freq-bounds")
        .withArgName("low,high")
        .hasArgs(2)
        .withValueSeparator(',')
        .isRequired(false)
        .withDescription("Use only genes which frequency lay in [low, high]. Must be comma-separated.")
        .create("b")
    );

    return options;
  }
}
