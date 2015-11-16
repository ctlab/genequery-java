package com.genequery.commons.search;


import com.genequery.commons.math.FisherTable;
import com.genequery.commons.models.Module;

/**
 * Created by Arbuzov Ivan.
 */
public class SearchResult implements Comparable<SearchResult> {
  private final String gse;
  private final String gpl;
  private final int moduleNumber;
  private double pvalue;
  private double logPvalue;
  private double empiricalPvalue;
  private double logEmpiricalPvalue;
  private final int intersectionSize;
  private final int moduleSize;
  private final FisherTable fisherTable;

  public FisherTable getFisherTable() {
    return fisherTable;
  }

  public String getGse() {
    return gse;
  }

  public String getGpl() {
    return gpl;
  }

  public int getModuleNumber() {
    return moduleNumber;
  }

  public double getLogPvalue() {
    return logPvalue;
  }

  public double getEmpiricalPvalue() {
    return empiricalPvalue;
  }

  public double getLogEmpiricalPvalue() {
    return logEmpiricalPvalue;
  }

  public int getIntersectionSize() {
    return intersectionSize;
  }

  public int getModuleSize() {
    return moduleSize;
  }

  public SearchResult(Module module, double pvalue, int intersectionSize, FisherTable fisherTable) {
    this.gse = module.getName().getGse();
    this.gpl = module.getName().getGpl();
    this.moduleNumber = module.getName().getModuleNumber();
    setPvalue(pvalue);
    this.moduleSize = module.getGenes().length;
    this.intersectionSize = intersectionSize;
    this.fisherTable = fisherTable;
  }

  public SearchResult(Module module, double pvalue, double empiricalPvalue, int intersectionSize, FisherTable fisherTable) {
    this(module, pvalue, intersectionSize, fisherTable);
    setEmpiricalPvalue(empiricalPvalue);
  }

  public void setEmpiricalPvalue(double empiricalPvalue) {
    this.empiricalPvalue = empiricalPvalue;
    this.logEmpiricalPvalue = empiricalPvalue != 0 ? Math.log10(empiricalPvalue) : Double.NEGATIVE_INFINITY;
  }

  public void setPvalue(double pvalue) {
    this.pvalue = pvalue;
    this.logPvalue = pvalue != 0 ? Math.log10(pvalue) : Double.NEGATIVE_INFINITY;
  }

  @Override
  public int compareTo(SearchResult other) {
    if (Math.abs(logPvalue - other.logPvalue) < 1e-323) {
      return 0;
    }
    return logPvalue > other.logPvalue ? 1 : -1;
  }

  public String getDataToStringLine() {
    return String.format(
        "%s\t%s\t%d\t%.2f\t%.2f\t%d\t%d",
        getGse(), getGpl(), getModuleNumber(),
        getLogPvalue(),
        getLogEmpiricalPvalue(),
        getIntersectionSize(),
        getModuleSize()
    );
  }

  @Override
  public String toString() {
    return getDataToStringLine();
  }
}
