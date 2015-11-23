package com.genequery.commons.search;


import com.genequery.commons.math.FisherTable;
import com.genequery.commons.models.Module;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Arbuzov Ivan.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SearchResult implements Comparable<SearchResult> {
  @NotNull private final String gse;
  @NotNull private final String gpl;
  private final int moduleNumber;
  private double pvalue;
  private double logPvalue;
  private double empiricalPvalue;
  private double logEmpiricalPvalue;
  private final int intersectionSize;
  private final int moduleSize;
  @NotNull private final FisherTable fisherTable;

  @NotNull
  public FisherTable getFisherTable() {
    return fisherTable;
  }

  @NotNull
  public String getGse() {
    return gse;
  }

  @NotNull
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

  public double getPvalue() {
    return pvalue;
  }

  public SearchResult(Module module, double pvalue, int intersectionSize, @NotNull FisherTable fisherTable) {
    this.gse = module.getName().getGse();
    this.gpl = module.getName().getGpl();
    this.moduleNumber = module.getName().getModuleNumber();
    setPvalue(pvalue);
    this.moduleSize = module.getGenes().length;
    this.intersectionSize = intersectionSize;
    this.fisherTable = fisherTable;
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
  public int compareTo(@NotNull SearchResult other) {
    if (Math.abs(logPvalue - other.logPvalue) < 1e-323) {
      return 0;
    }
    return logPvalue > other.logPvalue ? 1 : -1;
  }

  public String getDataToStringLine() {
    return String.format(
        "%s\t%s\t%d\t%.8e\t%.2f\t%.8e\t%.2f\t%d\t%d",
        getGse(), getGpl(), getModuleNumber(),
        getPvalue(),
        getLogPvalue(),
        getEmpiricalPvalue(),
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
