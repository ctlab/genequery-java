package com.genequery.commons.math;

import com.genequery.commons.utils.StringUtils;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherTable {
  public final int a;
  public final int b;
  public final int c;
  public final int d;

  public FisherTable(int a, int b, int c, int d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public int[] toArray() {
    return new int[]{a, b, c, d};
  }

  @Override
  public String toString() {
    return StringUtils.fmt("a={} b={} c={} d={}", a, b, c, d);
  }
}
