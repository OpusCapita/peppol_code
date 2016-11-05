package com.opuscapita.peppol.commons.container.route;

import java.util.Arrays;
import java.util.List;

/**
 * @author  KACENAR1
 * Date: 13.29.11
 */
public enum TransportType {
    /** Incoming from gateway to outbound */
    OUT_IN("OUT-in"),
    /** From reprocessing to outbound */
    OUT_REPROCESS("OUT-reprocess"),
    /** From gateway to Peppol, terminal step */
    OUT_PEPPOL("OUT-peppol"),
    /** From reprocessing to Peppol, terminal step */
    OUT_PEPPOL_FINAL("OUT-peppol-final"),
    /** Outbound validation failure */
    OUT_SMTP("OUT_smtp"),
    /** Just received from Peppol */
    IN_IN("IN-in"),
    /** Inbound to gateway, terminal step */
    IN_OUT("IN-out"),
    /** Inbound validation failure */
    IN_SMTP("IN-smtp"),
    /** Unknown */
    UNKNOWN("");

    private String type;

    TransportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private static final List<TransportType> ALLOWED_FOR_PERSISTENCE_TYPES = Arrays.asList(
            OUT_IN, OUT_REPROCESS, OUT_PEPPOL, OUT_PEPPOL_FINAL,
            IN_IN, IN_OUT);

    public static TransportType getTransportType(String type) {
        if (type == null) {
            return TransportType.UNKNOWN;
        }
        for (TransportType transportType : TransportType.values()) {
            if (transportType.type.equals(type)) {
                return transportType;
            }
        }
        return TransportType.UNKNOWN;
    }

    public static boolean isInbound(TransportType type) {
        return type == IN_IN || type == IN_OUT || type == IN_SMTP;
    }

    public static boolean allowedForPersistence(TransportType type) {
        return ALLOWED_FOR_PERSISTENCE_TYPES.contains(type);
    }

}
