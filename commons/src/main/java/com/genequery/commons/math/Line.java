package com.genequery.commons.math;

/**
 * Created by Arbuzov Ivan.
 */
public class Line {
  private final double k;
  private final double b;

  public Line(double k, double b) {
    this.k = k;
    this.b = b;
  }

  public Line(Point p1, Point p2) {
    k = (p2.getY()- p1.getY()) / (p2.getX() - p1.getX());
    b = p1.getY() - k * p1.getX();
  }

  public double f(double x) {
    return k * x + b;
  }

  public double getK() {
    return k;
  }

  public double getB() {
    return b;
  }
}
