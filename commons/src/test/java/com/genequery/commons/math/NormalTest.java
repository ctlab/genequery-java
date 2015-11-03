package com.genequery.commons.math;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arbuzov Ivan.
 */
public class NormalTest {

  @Test
  public void testCdf() throws Exception {
    double logPvalue = -33.89071337129947;
    double mean = -3.7172;
    double sigma = 1.0202;
    assertEquals(1.5176139736293816E-192, Normal.cdf(logPvalue, mean, sigma), 1e-323);
  }
}