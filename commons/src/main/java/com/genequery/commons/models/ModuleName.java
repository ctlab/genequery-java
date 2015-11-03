package com.genequery.commons.models;

/**
 * Created by Arbuzov Ivan.
 */
public class ModuleName {
    private final String gse;
    private final String gpl;
    private final int moduleNumber;

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
    }

    @Override
    public String toString() {
        return full();
    }

    public String full() {
        return gse + "_" + gpl + "#" + moduleNumber;
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
        return getGse() + "_" + getGpl();
    }
}

