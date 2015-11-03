package com.genequery.rest;

import com.genequery.commons.math.FisherExact;
import com.genequery.commons.math.Normal;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(FisherSearcher.class);

    /**
     * Quick test:
     * GSE_GPL#MODULE   INTERSECTION_SIZE   a b c d     p-val   log(p-val)  empirical-p-val     log(empirical-p-val)
     * GSE46356_GPL6246#4	39 665 2 5294   1.28613521237e-34 -33.8907133713 1.51761397339e-192 -191.818838683
     */
    public static List<SearchResult> search(DataSet dataSet, long[] query, double empPvalueThreshold) {
        Arrays.sort(query);
        Collection<Module> modules = dataSet.getModules();

        final TObjectIntHashMap<String> overlaps = new TObjectIntHashMap<>(modules.size());
        final TObjectIntHashMap<String> overlapsWithGSE = new TObjectIntHashMap<>();

        modules.forEach(
                module -> {
                    int intersectionLength = module.intersectionSize(query);
                    if (intersectionLength == 0) return;
                    
                    overlaps.put(module.getName().full(), intersectionLength);

                    String gseName = module.getName().getGseGpl();
                    overlapsWithGSE.adjustOrPutValue(gseName, intersectionLength, intersectionLength);
                }
        );

        FisherExact fisherExact = new FisherExact();
        final List<SearchResult> result = new ArrayList<>(overlaps.size());
        modules.forEach(module -> {
            int seriesOverlap = overlapsWithGSE.get(module.getName().getGseGpl());
            int a = overlaps.get(module.getName().full());
            int b = module.getGenes().length - a;
            int c = seriesOverlap - a;
            int d = 6000 - seriesOverlap - module.getGenes().length + a;

            if (a == 0) return;

            double pvalue = fisherExact.rightTail(a, b, c, d);
            double logPvalue = pvalue != 0 ? Math.log10(pvalue) : Double.NEGATIVE_INFINITY;
            double empiricalPvalue = calculateEmpiricalPvalue(dataSet.getSpecies(), query.length, logPvalue);

            if (empiricalPvalue > empPvalueThreshold) return;

            double logEmpiricalPvalue = empiricalPvalue != 0 ? Math.log10(empiricalPvalue) : Double.NEGATIVE_INFINITY;

            result.add(new SearchResult(module, logPvalue, logEmpiricalPvalue, a));
        });
        return result;
    }

    private static double calculateEmpiricalPvalue(Species species, int moduleSize, double logPvalue) {
        return Normal.cdf(logPvalue, getMean(species, moduleSize), getStd(species, moduleSize));
    }

    private static double getStd(Species species, int moduleSize) {
        if (species == Species.MOUSE) return 0.982519d + 0.000769d * moduleSize;
        if (species == Species.HUMAN) return 1.027662d + 0.000939d * moduleSize;
        throw new IllegalArgumentException("No empirical mean for species " + species);
    }

    private static double getMean(Species species, int moduleSize) {
        if (species == Species.MOUSE) return -3.06942d - 0.01322d * moduleSize;
        if (species == Species.HUMAN) return -2.2151d - 0.0187d * moduleSize;
        throw new IllegalArgumentException("No empirical std for species " + species);
    }
}
