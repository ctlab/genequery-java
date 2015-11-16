package com.genequery.commons.math;

import org.jetbrains.annotations.NotNull;

/**
 * ru.ifmo.gq.console.fisher.Fisher exact test (left tail).
 * <p>
 * This class is modified version of http://www.users.zetnet.co.uk/hopwood/tools/StatTests.java
 * <p>
 * Created by Arbuzov Ivan.
 */
public class FisherExactTest {

  /**
   * Genes count supposed to be involved in a GSE.
   */
  public static final int OVERESTIMATED_GENE_WORLD_SIZE = Integer.parseInt(System.getProperty("gene.world.size", "7000"));

  /**
   * Pre-calculated log factorial value.
   */
  private static final double[] logFactorial = new double[OVERESTIMATED_GENE_WORLD_SIZE + 1];

  static {
    logFactorial[0] = 0.0;
    for (int i = 1; i <= OVERESTIMATED_GENE_WORLD_SIZE; i++) {
      logFactorial[i] = logFactorial[i - 1] + Math.log(i);
    }
  }

  private FisherExactTest() {}

  /**
   * Calculate a right p-value for ru.ifmo.gq.console.fisher.Fisher's Exact Test.
   */
  public static double rightTail(int a, int b, int c, int d) {
    double p_sum = 0.0d;
    double p = calculateHypergeomP(a, b, c, d);
    while (c >= 0 && b >= 0) {
      p_sum += p;
      if (b == 0 || c == 0) break;
      ++a;
      --b;
      --c;
      ++d;
      p = calculateHypergeomP(a, b, c, d);
    }
    return p_sum;
  }

  /**
   * Calculate a right p-value for ru.ifmo.gq.console.fisher.Fisher's Exact Test.
   */
  public static double rightTail(@NotNull FisherTable fisherTable) {
    return rightTail(fisherTable.a, fisherTable.b, fisherTable.c, fisherTable.d);
  }

  private static double calculateHypergeomP(int a, int b, int c, int d) {
    return Math.exp(logFactorial[a + b] +
        logFactorial[c + d] +
        logFactorial[a + c] +
        logFactorial[b + d] -
        logFactorial[a + b + c + d] -
        logFactorial[a] -
        logFactorial[b] -
        logFactorial[c] -
        logFactorial[d]);
  }
}
