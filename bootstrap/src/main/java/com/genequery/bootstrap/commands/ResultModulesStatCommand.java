package com.genequery.bootstrap.commands;

import com.genequery.bootstrap.BootstrapOptions;
import com.genequery.bootstrap.Utils;
import com.genequery.commons.dao.GeneFrequencyDAO;
import com.genequery.commons.math.FisherTable;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.search.FisherSearcher;
import com.genequery.commons.search.SearchResult;
import com.genequery.commons.utils.StringUtils;
import org.apache.commons.cli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * Created by Arbuzov Ivan.
 */
public class ResultModulesStatCommand extends BaseCommand {

  private Random random = new Random();

  private Callable<Collection<SearchResult>> search(int requestLength,
                                                    List<Long> entrezIds,
                                                    DataSet dataSet,
                                                    Properties context) {
    return () -> {
      while (true) {
        long[] query = Utils.randomList(requestLength, entrezIds, random);
        Collection<SearchResult> results = FisherSearcher.searchPvalue(dataSet, query, context);
        if (results.isEmpty()) continue;
        return results;
      }
    };
  }

  @Override
  protected void _execute(CommandLine commandLine) throws IOException {
    String outputFilename = getOutFileName(commandLine);
    int[] samplesPerQuerySizes = getFits(commandLine);
    int threadCount = getThreadCount(commandLine);

    Species species = getSpecies(commandLine);
    String pathToData = getPathToData(commandLine);

    DataSet dataSet = Utils.readDataSet(species, pathToData);
    int[] partition = Utils.readPartition(commandLine.getOptionValue(BootstrapOptions.PARTITION));

    GeneFrequencyDAO geneFrequencyDAO = Utils.readGeneFrequency(species, pathToData);

    int[] freqBounds = new int[]{1, 100000};
    if (commandLine.hasOption(BootstrapOptions.FREQUENCY_BOUNDS)) {
      freqBounds = Arrays.stream(commandLine.getOptionValues(BootstrapOptions.FREQUENCY_BOUNDS))
        .mapToInt(Integer::parseInt)
        .toArray();
    }

    int resultLimit = Integer.parseInt(commandLine.getOptionValue(BootstrapOptions.FIRST_N_MODULES, "100000"));
    System.out.println("Limit result length for every request to " + resultLimit);

    boolean useTrueGseSize = useTrueGseSize(commandLine);
    System.out.println("Use " + (useTrueGseSize ? "true GSE size" : "6k") + " as gse size in Fisher exact test.");

    Path outputPath = Paths.get(outputFilename);
    System.out.println("Output path " + outputPath);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      writer.write("querySize\tfit\tfreqFrom\tfreqTo\tposition\tlogPvalue\tmoduleName\tmoduleSize\tfisher.a\tfisher.b\tfisher.c\tfisher.d\tintersectionFreqs\n");
      String lineFormat = "%d\t%d\t%d\t%d\t%d\t%.16f\t%s\t%d\t%d\t%d\t%d\t%d\t%s\n";
      for (int i = 0; i < freqBounds.length - 1; i++) {
        int freqFrom = freqBounds[i];
        int freqTo = freqBounds[i + 1];
        List<Long> entrezIds = Utils.readEntrezIDsBounded(freqFrom, freqTo, geneFrequencyDAO);
        runSearchInParallel(samplesPerQuerySizes, dataSet, partition, resultLimit, entrezIds, freqFrom, freqTo,
          useTrueGseSize, geneFrequencyDAO.entrezId2Frequency(), executor, writer, lineFormat);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } finally {
      executor.shutdownNow();
    }
  }

  private void runSearchInParallel(int[] fits,
                                   DataSet dataSet,
                                   int[] partition,
                                   int resultLimit,
                                   List<Long> entrezIds,
                                   int freqFrom, int freqTo,
                                   boolean useTrueGseSize,
                                   final Map<Long, Integer> entrez2freq,
                                   ExecutorService executor,
                                   BufferedWriter writer,
                                   String lineFormat) throws IOException, ExecutionException, InterruptedException {
    if (entrezIds.isEmpty()) {
      throw new IllegalArgumentException("Result entrez ID list is empty.");
    }

    Properties context = new Properties();
    context.put(FisherSearcher.USE_TRUE_GSE_SIZE, useTrueGseSize);

    long t = System.currentTimeMillis();
    for (int querySize : partition) {
      for (int fit : fits) {
        final List<Callable<Collection<SearchResult>>> callables = new ArrayList<>();
        for (int i = 0; i < fit; ++i) {
          callables.add(search(querySize, entrezIds, dataSet, context));
        }

        List<Future<Collection<SearchResult>>> futures = executor.invokeAll(callables);

        for (Future<Collection<SearchResult>> future : futures) {
          Collection<SearchResult> results = future.get();
          List<SearchResult> sortedLimited = results.stream().sorted().limit(resultLimit).collect(Collectors.toList());
          int position = 1;
          for (SearchResult result : sortedLimited) {
            String name = StringUtils.fmt("{}_{}#{}", result.getGse(), result.getGpl(), result.getModuleNumber());
            FisherTable ft = result.getFisherTable();
            String geneFreqs = result.getIntersection().stream()
              .mapToInt(entrez2freq::get)
              .mapToObj(String::valueOf)
              .collect(Collectors.joining(","));

            writer.write(String.format(
              lineFormat,
              querySize, fit, freqFrom, freqTo, position, result.getLogPvalue(), name, result.getModuleSize(),
              ft.a, ft.b, ft.c, ft.d, geneFreqs));
            position++;
          }
        }
        writer.flush();

        System.out.println(StringUtils.fmt("query size: {}, fit: {}, time: {}ms",
          querySize, fit, System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
      }
    }
    System.out.println();
  }
}
