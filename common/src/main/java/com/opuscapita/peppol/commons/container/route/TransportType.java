package com.opuscapita.peppol.commons.container.route;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.29.11
 * Time: 15:11
 */
public enum TransportType {
    OUT_IN("OUT-in"), OUT_REPROCESS("OUT-reprocess"), OUT_PEPPOL("OUT-peppol"), OUT_PEPPOL_FINAL("OUT-peppol-final"), OUT_SMTP("OUT-smtp"),
    IN_IN("IN-in"), IN_OUT("IN-out"), IN_SMTP("IN-smtp"), UNKNOWN("");

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
