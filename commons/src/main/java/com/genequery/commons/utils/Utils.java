package com.genequery.commons.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Arbuzov Ivan.
 */
public class Utils {
    public static String join(Collection collection, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for(Iterator iterator = collection.iterator(); iterator.hasNext(); sb.append((String)iterator.next())) {
            if(sb.length() != 0) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }
}
