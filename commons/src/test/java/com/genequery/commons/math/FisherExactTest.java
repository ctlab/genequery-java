package com.genequery.commons.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherExactTest {

  @Test
  public void testRightTail() throws Exception {
    FisherExact fisherExact = new FisherExact();
    double eps = 1e-323;
    assertEquals(5.8689429962759094E-24, fisherExact.rightTail(16, 81, 11, 6892), eps);
    assertEquals(2.3879574661733384E-28, fisherExact.rightTail(16, 76, 3, 6805), eps);
    assertEquals(1.909706400598598E-15, fisherExact.rightTail(16, 80, 59, 6845), eps);
  }
}