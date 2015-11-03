package com.genequery.rest;


import com.genequery.commons.models.Module;

/**
 * Created by Arbuzov Ivan.
 */
public class SearchResult implements Comparable<SearchResult> {
    private final String gse;
    private final String gpl;
    private final int moduleNumber;
    private final double logPvalue;
    private final double logEmpiricalPvalue;
    private final int intersectionSize;
    private final int moduleSize;

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

    public double getLogEmpiricalPvalue() {
        return logEmpiricalPvalue;
    }

    public int getIntersectionSize() {
        return intersectionSize;
    }

    public int getModuleSize() {
        return moduleSize;
    }

    public SearchResult(Module module, double logPvalue, double logEmpiricalPvalue, int intersectionSize) {
        this.gse = module.getName().getGse();
        this.gpl = module.getName().getGpl();
        this.moduleNumber = module.getName().getModuleNumber();
        this.logPvalue = logPvalue;
        this.logEmpiricalPvalue = logEmpiricalPvalue;
        this.moduleSize = module.getGenes().length;
        this.intersectionSize = intersectionSize;
    }

    @Override
    public int compareTo(SearchResult other) {
        if (Math.abs(logPvalue - other.logPvalue) < 1e-323) {
            return 0;
        }
        return logPvalue > other.logPvalue ? 1 : -1;
    }

    public String getDataInStringLine() {
        return String.format(
                "%s\t%s\t%d\t%.2f\t%.2f\t%d\t%d",
                getGse(), getGpl(), getModuleNumber(), getLogPvalue(), getLogEmpiricalPvalue(), getIntersectionSize(),
                getModuleSize()
        );
    }

    @Override
    public String toString() {
        return getDataInStringLine();
    }
}
