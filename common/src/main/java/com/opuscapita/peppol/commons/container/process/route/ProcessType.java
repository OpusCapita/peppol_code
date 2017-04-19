package com.opuscapita.peppol.commons.container.process.route;

/**
 * @author  KACENAR1
 * Date: 13.29.11
 */
public enum ProcessType {
    /** Incoming from gateway to outbound */
    OUT_IN,
    /** From reprocessing to outbound */
    OUT_REPROCESS,
    /** From gateway to Peppol, terminal step */
    OUT_PEPPOL,
    /** From reprocessing to Peppol, terminal step */
    OUT_PEPPOL_FINAL,
    /** Outbound validation failure */
    OUT_SMTP,
    /** Just received from Peppol */
    IN_IN,
    /** Inbound to gateway, terminal step */
    IN_OUT,
    /** Inbound validation failure */
    IN_SMTP,
    /** Unknown */
    UNKNOWN,

    // additional types defined for the new solution
    OUT_FILE_TO_MQ, OUT_PREPROCESS, OUT_VALIDATION, OUT_OUTBOUND,
    IN_INBOUND, IN_PREPROCESS, IN_VALIDATION, IN_MQ_TO_FILE,
    IN_REPROCESS, REST,
    IN_TEST, OUT_TEST, TEST, IN_ROUTING, OUT_ROUTING, WEB;

    /**
     * @return Returns true if this transport type belongs to the inbound flow
     */
    public boolean isInbound() {
        return this.toString().startsWith("IN_");
    }

}
