package com.genequery.commons.models;

/**
 * Created by Arbuzov Ivan.
 */
public class ModuleName {
  private final String gse;
  private final String gpl;
  private final int moduleNumber;
  private final String fullName;
  private final String gseGpl;

  /**
   * @param wholeName module ID in the form of GSE[num]_GPL[num]#MODULE_NUMBER[module],
   *                  e.g. GSE43781_GPL7202#51
   */
  public ModuleName(String wholeName) {
    String[] gseGplAndNumber = wholeName.split("#");
    moduleNumber = Integer.parseInt(gseGplAndNumber[1]);

    String[] gseAndGpl = gseGplAndNumber[0].split("_");
    gse = gseAndGpl[0];
    gpl = gseAndGpl[1];
    gseGpl = gse + "_" + gpl;
    fullName = gseGpl + "#" + moduleNumber;
  }

  @Override
  public String toString() {
    return full();
  }

  public String full() {
    return fullName;
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

  public String getGseGpl() {
    return gseGpl;
  }
}

