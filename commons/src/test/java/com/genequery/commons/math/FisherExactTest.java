package com.genequery.commons.math;

import junit.framework.TestCase;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherExactTest extends TestCase {

  public void testRightTail() throws Exception {
    FisherExact fisherExact = new FisherExact();
    assertEquals(5.8689429962759094E-24, fisherExact.rightTail(16, 81, 11, 6892));
    assertEquals(2.3879574661733384E-28, fisherExact.rightTail(16, 76, 3, 6805));
    assertEquals(1.909706400598598E-15, fisherExact.rightTail(16, 80, 59, 6845));
  }
}