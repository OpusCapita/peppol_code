package com.opuscapita.peppol.commons.container.route;

/**
 * Single endpoint of the module/process/whatever in a whole route.
 *
 * @author Sergejs.Roze
 */
public enum Endpoint {
    PEPPOL,
    GATEWAY,
    REST,
    REPROCESS,
    RETRY
}
