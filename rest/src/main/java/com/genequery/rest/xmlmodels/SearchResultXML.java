package com.genequery.rest.xmlmodels;

import com.genequery.commons.search.SearchResult;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Arbuzov Ivan.
 */
@XmlRootElement
public class SearchResultXML {
  private String gse;
  private String gpl;
  private int moduleNumber;
  private double pvalue;
  private double logPvalue;
  private double empiricalPvalue;
  private double logEmpiricalPvalue;
  private int intersectionSize;
  private int moduleSize;
  private int[] fisherTable;

  public SearchResultXML() {}

  public String getGse() {
    return gse;
  }

  public void setGse(String gse) {
    this.gse = gse;
  }

  public String getGpl() {
    return gpl;
  }

  public void setGpl(String gpl) {
    this.gpl = gpl;
  }

  public int getModuleNumber() {
    return moduleNumber;
  }

  public void setModuleNumber(int moduleNumber) {
    this.moduleNumber = moduleNumber;
  }

  public double getPvalue() {
    return pvalue;
  }

  public void setPvalue(double pvalue) {
    this.pvalue = pvalue;
  }

  public double getLogPvalue() {
    return logPvalue;
  }

  public void setLogPvalue(double logPvalue) {
    this.logPvalue = logPvalue;
  }

  public double getEmpiricalPvalue() {
    return empiricalPvalue;
  }

  public void setEmpiricalPvalue(double empiricalPvalue) {
    this.empiricalPvalue = empiricalPvalue;
  }

  public double getLogEmpiricalPvalue() {
    return logEmpiricalPvalue;
  }

  public void setLogEmpiricalPvalue(double logEmpiricalPvalue) {
    this.logEmpiricalPvalue = logEmpiricalPvalue;
  }

  public int getIntersectionSize() {
    return intersectionSize;
  }

  public void setIntersectionSize(int intersectionSize) {
    this.intersectionSize = intersectionSize;
  }

  public int getModuleSize() {
    return moduleSize;
  }

  public void setModuleSize(int moduleSize) {
    this.moduleSize = moduleSize;
  }

  public int[] getFisherTable() {
    return fisherTable;
  }

  public void setFisherTable(int[] fisherTable) {
    this.fisherTable = fisherTable;
  }

  public static SearchResultXML createFromSearchResult(SearchResult searchResult) {
    SearchResultXML resultXML = new SearchResultXML();
    resultXML.setGse(searchResult.getGse());
    resultXML.setGpl(searchResult.getGpl());
    resultXML.setModuleNumber(searchResult.getModuleNumber());
    resultXML.setModuleSize(searchResult.getModuleSize());
    resultXML.setPvalue(searchResult.getPvalue());
    resultXML.setLogPvalue(searchResult.getLogPvalue());
    resultXML.setEmpiricalPvalue(searchResult.getEmpiricalPvalue());
    resultXML.setLogEmpiricalPvalue(searchResult.getLogEmpiricalPvalue());
    resultXML.setIntersectionSize(searchResult.getIntersectionSize());
    resultXML.setFisherTable(searchResult.getFisherTable().toArray());
    return resultXML;
  }
}
