package com.genequery.commons.math;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Arbuzov Ivan.
 */
public class PolylineTest {

  @Test
  public void testF() throws Exception {
    double delta = 1e-12;

    Point p1 = new Point(5, -3.6562114761963302);
    Point p2 = new Point(6, -3.7719114404393554);
    Point p3 = new Point(10, -3.9210790890265040);
    Point p4 = new Point(15, 0.6115200344396203);
    Point p5 = new Point(20, 0.5558708552395484);

    Line rline = new Line(new Point(0, 0), new Point(1, 1));
    Polyline pline = new Polyline(p1, p3, p2, p5, p4);

    assertEquals(rline.f(4), pline.f(4, rline), delta);
    assertEquals(rline.f(21), pline.f(21, rline), delta);

    Line line12 = new Line(p1, p2);
    Line line23 = new Line(p3, p2);
    assertEquals(line12.f(p1.getX()), pline.f(p1.getX(), rline), delta);
    assertEquals(line12.f(p2.getX()), pline.f(p2.getX(), rline), delta);
    assertEquals(line23.f(p2.getX()), pline.f(p2.getX(), rline), delta);

    int x45 = 17;
    Line line45 = new Line(p4, p5);
    assertEquals(line45.f(x45), pline.f(x45, rline), delta);
    assertEquals(line45.f(p4.getX()), pline.f(p4.getX(), rline), delta);
    assertEquals(line45.f(p5.getX()), pline.f(p5.getX(), rline), delta);
  }
}