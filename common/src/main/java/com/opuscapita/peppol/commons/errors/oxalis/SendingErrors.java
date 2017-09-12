package com.opuscapita.peppol.commons.errors.oxalis;

/**
 * List of possible issues in sending part of Oxalis
 *
 * @author Sergejs.Roze
 */
public enum SendingErrors {

    /** Error in input data */
    DATA_ERROR,
    /** Failed to connect for some reason */
    CONNECTION_ERROR,
    /** Receiving AP returned code other than 200 */
    RECEIVING_AP_ERROR,
    /** Unknown recipient or unsupported data format, currently not distinguishable */
    UNKNOWN_RECIPIENT,
    UNSUPPORTED_DATA_FORMAT,
    /** Security issue - expired, invalid, or unknown certificates */
    SECURITY_ERROR,
    /** All other errors */
    OTHER_ERROR

}
