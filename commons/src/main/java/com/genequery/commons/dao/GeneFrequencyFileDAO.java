package com.genequery.commons.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Arbuzov Ivan.
 */
public class GeneFrequencyFileDAO implements GeneFrequencyDAO {

  private final Map<Long, Integer> gene2freq;
  private final Map<Long, Double> gene2prob;
  private final List<Long> genesInFreqOrder;

  public GeneFrequencyFileDAO(Path path, String delimiter) throws IOException {
    gene2freq = new HashMap<>();
    gene2prob = new HashMap<>();
    genesInFreqOrder = new ArrayList<>();

    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> {
        String[] parts = line.split(delimiter);
        long id = Long.parseLong(parts[0]);
        genesInFreqOrder.add(id);
        gene2freq.put(id, Integer.parseInt(parts[1]));
        if (parts.length > 2) {
          gene2prob.put(id, Double.parseDouble(parts[2]));
        }
      });
    }
    if (gene2prob.isEmpty()) {
      populateGene2Prob();
    }
  }

  private void populateGene2Prob() {
    double sum = gene2freq.values().stream().mapToInt(v -> v).sum();
    for (Long gene : gene2freq.keySet()) {
      gene2prob.put(gene, gene2freq.get(gene) / sum);
    }
  }

  @Override
  public Map<Long, Integer> entrezId2Frequency() {
    return gene2freq;
  }

  @Override
  public Map<Long, Double> entrezId2Prob() {
    return gene2prob;
  }

  @Override
  public List<Long> getGenesSortedByFreq() {
    return genesInFreqOrder;
  }
}
