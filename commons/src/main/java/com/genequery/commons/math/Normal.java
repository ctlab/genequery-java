package com.genequery.commons.math;

import org.apache.commons.math3.special.Gamma;

import static org.apache.commons.math3.special.Gamma.regularizedGammaP;

/**
 * Created by Arbuzov Ivan.
 */
public class Normal {
  public static final double SQRT2 = Math.sqrt(2.0);
  public static final double EPS = 1.0e-322;

  /**
   * Hacked version of Apache's NormalDistribution::cumulativeDistribution function
   * aimed to be more precise on very small log(p-value).
   *
   * @param logPvalue log(p-value)
   * @param mean      mean
   * @param sigma     sigma
   * @return value of the cdf function
   */
  public static double cdf(double logPvalue, double mean, double sigma) {
    if (logPvalue == Double.NEGATIVE_INFINITY) return 0;

    double x = (logPvalue - mean) / (sigma * SQRT2);
    double a = 0.5; // a from regularized Gamma P/Q
    double xSqr = x * x;
    double regularizedGamma;
    if (xSqr >= a + 1.0) {
      regularizedGamma = Gamma.regularizedGammaQ(a, xSqr, EPS, 100000);
      return x < 0 ? 0.5 * regularizedGamma : 0.5 * (2 - regularizedGamma);
    }

    return 0.5 * (1 + regularizedGammaP(a, xSqr, 1.0e-312, 100000) * (x < 0 ? -1 : 1));
  }
}
