package com.genequery.commons.math;

/**
 * ru.ifmo.gq.console.fisher.Fisher exact test (left tail).
 *
 * This class is modified version of http://www.users.zetnet.co.uk/hopwood/tools/StatTests.java
 *
 * Created by Arbuzov Ivan.
 */
public class FisherExact {

    /**
     * Pre-calculated log factorial value.
     */
    private final double[] logFactorial;

    /**
     * Genes count supposed to be involved in a GSE.
     */
    public static final int OVERESTIMATED_GENE_WORLD_SIZE = 7000;

    public FisherExact() {
        logFactorial = new double[OVERESTIMATED_GENE_WORLD_SIZE + 1];
        logFactorial[0] = 0.0;
        for (int i = 1; i <= OVERESTIMATED_GENE_WORLD_SIZE; i++) {
            logFactorial[i] = logFactorial[i-1] + Math.log(i);
        }
    }

    /**
     *  Calculate a right p-value for ru.ifmo.gq.console.fisher.Fisher's Exact Test.
     *
     *  Sanity tests:
     *  16, 81, 11, 6892 5.86894299623e-24
     *  16, 80, 59, 6845 1.90970640055e-15
     *  16, 76, 3, 6905 1.89727917213e-28
     *  */
    public double rightTail(int a, int b, int c, int d) {
        double p_sum = 0.0d;
        double p = calculateHypergeomP(a, b, c, d);
        while (c >= 0 && b >= 0) {
            p_sum += p;
            if (b == 0 || c == 0) break;
            ++a; --b; --c; ++d;
            p = calculateHypergeomP(a, b, c, d);
        }
        return p_sum;
    }

    private double calculateHypergeomP(int a, int b, int c, int d) {
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
