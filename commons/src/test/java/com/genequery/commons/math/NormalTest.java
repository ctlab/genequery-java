package com.genequery.commons.math;

import junit.framework.TestCase;

/**
 * Created by Arbuzov Ivan.
 */
public class NormalTest extends TestCase {

    public void testCdf() throws Exception {
        double logPvalue = -33.89071337129947;
        double mean = -3.7172;
        double sigma = 1.0202;
        assertEquals(Normal.cdf(logPvalue, mean, sigma), 1.5176139736293816E-192);
    }
}