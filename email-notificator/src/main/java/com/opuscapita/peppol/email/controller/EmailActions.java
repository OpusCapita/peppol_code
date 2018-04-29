package com.opuscapita.peppol.email.controller;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sergejs.Roze
 */
class EmailActions {
    static final String NOTHING = "nothing";
    static final String EMAIL_AP = "email-ap";
    static final String EMAIL_CUSTOMER = "email-customer";
    static final String SNC_TICKET = "snc-ticket";

    static void validate(@NotNull List<String> actions) {
        Set<String> set = new HashSet<>(actions);

        if (set.contains(NOTHING) && set.size() > 1) {
            throw new IllegalArgumentException("Actions contain '" + NOTHING + "' but contains more actions: " + set);
        }

        if (set.contains(SNC_TICKET)) {
            if (!set.contains(EMAIL_AP) && !set.contains(EMAIL_CUSTOMER)) {
                throw new IllegalArgumentException(
                        "SNC ticket creation requested but no email recipient defined, please use either '" + EMAIL_AP + "' or/and '" + EMAIL_CUSTOMER);
            }
        }

        StringBuilder unknown = new StringBuilder();
        for (String action : set) {
            switch (action) {
                case NOTHING:
                case EMAIL_AP:
                case EMAIL_CUSTOMER:
                case SNC_TICKET:
                    break;
                default:
                    unknown.append(action).append(", ");
            }
        }
        if (unknown.length() > 0) {
            throw new IllegalArgumentException("Unknown action(s) defined: " + unknown + "please check settings");
        }
    }

}
