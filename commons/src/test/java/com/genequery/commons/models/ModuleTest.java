package com.genequery.commons.models;

import junit.framework.TestCase;

/**
 * Created by Arbuzov Ivan.
 */
public class ModuleTest extends TestCase {

  public void testIntersectionSizeALongerB() throws Exception {
    Module module = new Module("GSE1_GPL2#3", Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.intersectionSize(new long[]{2, 5, 6, 8, 10, 11});
    assertEquals(2, res);
  }

  public void testIntersectionSizeAShorterB() throws Exception {
    Module module = new Module("GSE1_GPL2#3", Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.intersectionSize(new long[]{2, 5});
    assertEquals(1, res);
  }

  public void testIntersectionSizeNoInters1() throws Exception {
    Module module = new Module("GSE1_GPL2#3", Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.intersectionSize(new long[]{7});
    assertEquals(0, res);
  }

  public void testIntersectionSizeNoInters2() throws Exception {
    Module module = new Module("GSE1_GPL2#3", Species.HUMAN, new long[]{1});
    int res = module.intersectionSize(new long[]{2, 3, 4});
    assertEquals(0, res);
  }

  public void testIntersectionSizeEmptyQuery() throws Exception {
    Module module = new Module("GSE1_GPL2#3", Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.intersectionSize(new long[]{});
    assertEquals(0, res);
  }
}