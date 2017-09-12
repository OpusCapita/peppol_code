package com.opuscapita.peppol.commons.errors.oxalis;

/**
 * List of possible issues in sending part of Oxalis
 *
 * @author Sergejs.Roze
 */
public enum SendingErrors {

    /** Error in input data */
    DATA_ERROR(false),
    /** Failed to connect for some reason */
    CONNECTION_ERROR(true),
    /** Receiving AP returned code other than 200 */
    RECEIVING_AP_ERROR(true),
    /** Unknown recipient or unsupported data format, currently not distinguishable */
    UNKNOWN_RECIPIENT(false),
    UNSUPPORTED_DATA_FORMAT(false),
    /** Security issue - expired, invalid, or unknown certificates */
    SECURITY_ERROR(true),
    /** All other errors */
    OTHER_ERROR(false);

    private boolean temporary;

    SendingErrors(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isTemporary() {
        return temporary;
    }

}
