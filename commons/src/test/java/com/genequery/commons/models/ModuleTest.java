package com.genequery.commons.models;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Arbuzov Ivan.
 */
public class ModuleTest {
  
  private final ModuleName name = new ModuleName("GSE123_GPL123#1");

  @Test
  public void testIntersectionSizeALongerB() throws Exception {
    Module module = new Module(name, Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    List<Long> res = module.getIntersection(new long[]{2, 5, 6, 8, 10, 11});
    assertEquals(2, res.size());
    assertTrue(res.contains((long)5));
  }

  @Test
  public void testIntersectionSizeAShorterB() throws Exception {
    Module module = new Module(name, Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.getIntersection(new long[]{2, 5}).size();
    assertEquals(1, res);
  }

  @Test
  public void testIntersectionSizeNoInters1() throws Exception {
    Module module = new Module(name, Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.getIntersection(new long[]{7}).size();
    assertEquals(0, res);
  }

  @Test
  public void testIntersectionSizeNoInters2() throws Exception {
    Module module = new Module(name, Species.HUMAN, new long[]{1});
    int res = module.getIntersection(new long[]{2, 3, 4}).size();
    assertEquals(0, res);
  }

  @Test
  public void testIntersectionSizeEmptyQuery() throws Exception {
    Module module = new Module(name, Species.HUMAN, new long[]{1, 3, 4, 5, 6});
    int res = module.getIntersection(new long[]{}).size();
    assertEquals(0, res);
  }
}