package com.opuscapita.peppol.test.tools.smoke.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by gamanse1 on 2017.05.22..
 */
public class StringJoinUtils {

    public static String join(Collection collection, String separator) {
        StringBuffer result = new StringBuffer();

        for(Iterator iter = collection.iterator(); iter.hasNext(); result.append((String)iter.next())) {
            if(result.length() != 0) {
                result.append(separator);
            }
        }

        return result.toString();
    }
}
