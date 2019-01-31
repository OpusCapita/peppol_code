package com.opuscapita.peppol.commons.errors.oxalis;

/**
 * List of possible issues in sending part of Oxalis
 *
 * @author Sergejs.Roze
 */
public enum SendingErrors {

    /** Error with the document itself, e.g. empty file */
    DOCUMENT_ERROR(false),

    /** Data error inside the document, no-retry */
    DATA_ERROR(false),

    /** Failed to connect for some reason */
    CONNECTION_ERROR(true),

    /** Receiving AP doesn't return 200, possibly retry */
    RECEIVING_AP_ERROR(true),

    /** Recipient is not registered in SMP, no-retry */
    UNKNOWN_RECIPIENT(false),

    /** Doc format for the recipient is not registered in SMP */
    UNSUPPORTED_DATA_FORMAT(false),

    /** Security issue - expired, invalid, or unknown certificates */
    SECURITY_ERROR(false),

    /** Issue with file validation, used in MLR reporter and shouldn't be used elsewhere */
    VALIDATION_ERROR(false),

    /** All other errors */
    OTHER_ERROR(false);

    private boolean isRetryable;

    SendingErrors(boolean isRetryable) {
        this.isRetryable = isRetryable;
    }

    public boolean isRetryable() {
        return isRetryable;
    }

    public boolean requiresNotification() {
        return SendingErrors.DATA_ERROR.equals(this) ||
               SendingErrors.DOCUMENT_ERROR.equals(this) ||
               SendingErrors.UNKNOWN_RECIPIENT.equals(this) ||
               SendingErrors.UNSUPPORTED_DATA_FORMAT.equals(this);
    }

    public boolean requiresTicketCreation() {
        return SendingErrors.OTHER_ERROR.equals(this) ||
               SendingErrors.SECURITY_ERROR.equals(this) ||
               SendingErrors.CONNECTION_ERROR.equals(this) ||
               SendingErrors.RECEIVING_AP_ERROR.equals(this);
    }

}
