package com.opuscapita.peppol.events.persistence.model.util;

import java.sql.Timestamp;

/**
 * Created by Daniil on 13.07.2016.
 */
public class TimeStampComparison {
    public static int compare(Timestamp first, Timestamp second) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        try {
            if (first.equals(second)) {
                return EQUAL;
            }
            if (first.after(second)) {
                return BEFORE;
            }
            if (first.before(second)) {
                return AFTER;
            }
        } catch (NullPointerException pass) {
            return AFTER;
        }

        return EQUAL;
    }
}
