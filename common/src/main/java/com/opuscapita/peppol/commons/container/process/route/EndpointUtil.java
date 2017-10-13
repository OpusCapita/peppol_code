package com.opuscapita.peppol.commons.container.process.route;

public class EndpointUtil {
    public static boolean isTerminal(Endpoint endpoint) {
        boolean result = false;
        switch (endpoint.getType()) {
            case OUT_OUTBOUND:
            case OUT_PEPPOL_FINAL:
            case IN_MQ_TO_FILE:
                result = true;
                break;
        }
        return result;
    }
}
