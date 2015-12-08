package com.genequery.commons.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Arbuzov Ivan.
 */
public class LineTest {

  @Test
  public void testF() throws Exception {
    double delta = 1e-12;

    Point p1 = new Point(1, 6);
    Point p2 = new Point(3, 2);
    Line line = new Line(p1, p2);

    assertEquals(-2, line.getK(), delta);
    assertEquals(8, line.getB(), delta);
    assertEquals(p1.getY(), line.f(p1.getX()), delta);
    assertEquals(4, line.f(2), delta);
  }
}