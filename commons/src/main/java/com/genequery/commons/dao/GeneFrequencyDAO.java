package com.genequery.commons.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by Arbuzov Ivan.
 */
public interface GeneFrequencyDAO {
  Map<Long, Integer> entrezId2Frequency();
  Map<Long, Double> entrezId2Prob();
  List<Long> getGenesSortedByFreq();
}
