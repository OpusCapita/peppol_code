package com.opuscapita.peppol.commons.container.metadata;

/**
 * Simple POJO representation of eu.peppol.PeppolMessageMetaData for JSON conversion.
 *
 * Created by KACENAR1 on 13.04.2015
 */
public class PeppolMessageMetadata {
    /** The PEPPOL Message Identifier, supplied in the SBDH when using AS2 */
    private String messageId;

    /** The PEPPOL Participant Identifier of the end point of the receiver */
    private String recipientId;

    private String senderId;
    private String documentTypeIdentifier;
    private String profileTypeIdentifier;

    private String sendingAccessPoint;
    private String receivingAccessPoint;

    private String protocol;
    private String userAgent;
    private String userAgentVersion;
    private String sendersTimeStamp;
    private String receivedTimeStamp;

    private String transmissionId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public void setDocumentTypeIdentifier(String documentTypeIdentifier) {
        this.documentTypeIdentifier = documentTypeIdentifier;
    }

    public String getProfileTypeIdentifier() {
        return profileTypeIdentifier;
    }

    public void setProfileTypeIdentifier(String profileTypeIdentifier) {
        this.profileTypeIdentifier = profileTypeIdentifier;
    }

    public String getSendingAccessPoint() {
        return sendingAccessPoint;
    }

    public void setSendingAccessPoint(String sendingAccessPoint) {
        this.sendingAccessPoint = sendingAccessPoint;
    }

    public String getReceivingAccessPoint() {
        return receivingAccessPoint;
    }

    public void setReceivingAccessPoint(String receivingAccessPoint) {
        this.receivingAccessPoint = receivingAccessPoint;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgentVersion() {
        return userAgentVersion;
    }

    public void setUserAgentVersion(String userAgentVersion) {
        this.userAgentVersion = userAgentVersion;
    }

    public String getSendersTimeStamp() {
        return sendersTimeStamp;
    }

    public void setSendersTimeStamp(String sendersTimeStamp) {
        this.sendersTimeStamp = sendersTimeStamp;
    }

    public String getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(String receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public String getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(String transmissionId) {
        this.transmissionId = transmissionId;
    }
}
