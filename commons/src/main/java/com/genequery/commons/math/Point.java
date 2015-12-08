package com.genequery.commons.math;

/**
 * Created by Arbuzov Ivan.
 */
public class Point implements Comparable<Point> {
  private final double x;
  private final double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  @Override
  public int compareTo(Point o) {
    if (Math.abs(getX() - o.getX()) < 1e-300) {
      if (Math.abs(getY() - o.getY()) < 1e-300) {
        return 0;
      }
      if (getY() > o.getY()) {
        return 1;
      }
      return -1;
    }
    if (getX() > o.getX()) {
      return 1;
    }
    return -1;
  }
}
