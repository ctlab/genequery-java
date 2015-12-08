package com.genequery.commons.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Arbuzov Ivan.
 */
public class PointTest {

  @Test
  public void testCompareToGreat1() throws Exception {
    Point p1 = new Point(1, 0);
    Point p2 = new Point(0, 1);
    assertTrue(p1.compareTo(p2) > 0);
  }

  @Test
  public void testCompareToGreat2() throws Exception {
    Point p1 = new Point(1, 1);
    Point p2 = new Point(1, 0);
    assertTrue(p1.compareTo(p2) > 0);
  }

  @Test
  public void testCompareToLess() throws Exception {
    Point p1 = new Point(1, 0);
    Point p2 = new Point(1, 1);
    assertTrue(p1.compareTo(p2) < 0);
  }

  @Test
  public void testCompareToEqual() throws Exception {
    Point p1 = new Point(1, 1);
    Point p2 = new Point(1, 1);
    assertTrue(p1.compareTo(p2) == 0);
  }
}