package com.genequery.commons.math;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Arbuzov Ivan.
 */
public class Polyline {
  private final Point[] points;


  public Polyline(Point ... points) {
    if (points.length < 2) {
      throw new IllegalArgumentException(
        "Points array must contain at least 2 points, " + points.length + " is given instead.");
    }
    this.points = points;
    Arrays.sort(this.points);
  }

  public Polyline(Collection<Point> points) {
    this(points.stream().toArray(Point[]::new));
  }

  public double f(double x, Line defaultRegression) {
    for (int i = 0; i < points.length - 1; i++) {
      Point p1 = points[i];
      Point p2 = points[i + 1];
      if (p1.getX() <= x && x <= p2.getX()) {
        return new Line(p1, p2).f(x);
      }
    }
    return defaultRegression.f(x);
  }
}
