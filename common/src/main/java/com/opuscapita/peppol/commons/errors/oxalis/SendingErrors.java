package com.opuscapita.peppol.commons.errors.oxalis;

/**
 * List of possible issues in sending part of Oxalis
 *
 * @author Sergejs.Roze
 */
public enum SendingErrors {

    /** Error with the document itself, e.g. empty file */
    DOCUMENT_ERROR(false),
    /** Error inside the document */
    DATA_ERROR(false),
    /** Failed to connect for some reason */
    CONNECTION_ERROR(true),
    /** Receiving AP returned code other than 200 */
    RECEIVING_AP_ERROR(true),
    /** Unknown recipient or unsupported data format, currently not distinguishable */
    UNKNOWN_RECIPIENT(false),
    UNSUPPORTED_DATA_FORMAT(false),
    /** Security issue - expired, invalid, or unknown certificates */
    SECURITY_ERROR(false),
    /** Issue with file validation, used in MLR reporter and shouldn't be used elsewhere */
    VALIDATION_ERROR(false),
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
