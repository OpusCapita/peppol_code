package com.opuscapita.peppol.commons.container.metadata;

import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.transmission.TransmissionResult;
import no.difi.vefa.peppol.common.model.Header;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Simple POJO representation of eu.peppol.PeppolMessageMetaData for JSON conversion.
 */
public class PeppolMessageMetadata {

    private static final String OC_AP_COMMON_NAME = "C=FI, O=OpusCapita Solutions Oy, OU=PEPPOL PRODUCTION AP, CN=PNO000104";
    /**
     * The PEPPOL Message Identifier, supplied in the SBDH when using AS2
     */
    private String messageId;

    private String transmissionId;

    private String senderId;
    private String recipientId;
    private Date sendersTimeStamp;
    private Date receivedTimeStamp;
    private String sendingAccessPoint;
    private String receivingAccessPoint;

    private String documentTypeIdentifier;
    private String profileTypeIdentifier;

    private String instanceType;
    private String instanceTypeVersion;
    private String instanceTypeStandard;

    private String protocol;
    private String userAgent;
    private String userAgentVersion;

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

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getInstanceTypeVersion() {
        return instanceTypeVersion;
    }

    public void setInstanceTypeVersion(String instanceTypeVersion) {
        this.instanceTypeVersion = instanceTypeVersion;
    }

    public String getInstanceTypeStandard() {
        return instanceTypeStandard;
    }

    public void setInstanceTypeStandard(String instanceTypeStandard) {
        this.instanceTypeStandard = instanceTypeStandard;
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

    public Date getSendersTimeStamp() {
        return sendersTimeStamp;
    }

    public void setSendersTimeStamp(Date sendersTimeStamp) {
        this.sendersTimeStamp = sendersTimeStamp;
    }

    public Date getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(Date receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public String getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(String transmissionId) {
        this.transmissionId = transmissionId;
    }

    // File coming from business platforms to our transport service
    public static PeppolMessageMetadata create(TransmissionResult transmissionResult) {
        Header header = transmissionResult.getHeader();

        PeppolMessageMetadata metadata = new PeppolMessageMetadata();
        metadata.setMessageId(header.getIdentifier().getIdentifier());
        metadata.setRecipientId(header.getReceiver().getIdentifier());
        metadata.setSenderId(header.getSender().getIdentifier());
        metadata.setDocumentTypeIdentifier(header.getDocumentType().getIdentifier());
        metadata.setProfileTypeIdentifier(header.getProcess().getIdentifier());
        metadata.setSendingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setReceivingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setProtocol(transmissionResult.getProtocol().getIdentifier());
        metadata.setSendersTimeStamp(transmissionResult.getTimestamp());
        metadata.setReceivedTimeStamp(new Date());
        metadata.setTransmissionId(header.getIdentifier().getIdentifier());
        metadata.setInstanceType(header.getInstanceType().getType());
        metadata.setInstanceTypeVersion(header.getInstanceType().getVersion());
        metadata.setInstanceTypeStandard(header.getInstanceType().getStandard());

        return metadata;
    }

    // File coming from network to our inbound..
    public static PeppolMessageMetadata create(InboundMetadata inboundMetadata) {
        Header header = inboundMetadata.getHeader();
        X509Certificate certificate = inboundMetadata.getCertificate();
        X500Principal principal = certificate.getSubjectX500Principal();

        PeppolMessageMetadata metadata = new PeppolMessageMetadata();
        metadata.setMessageId(header.getIdentifier().getIdentifier());
        metadata.setRecipientId(header.getReceiver().getIdentifier());
        metadata.setSenderId(header.getSender().getIdentifier());
        metadata.setDocumentTypeIdentifier(header.getDocumentType().getIdentifier());
        metadata.setProfileTypeIdentifier(header.getProcess().getIdentifier());
        metadata.setSendingAccessPoint(principal.getName());
        metadata.setReceivingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setProtocol(inboundMetadata.getProtocol().getIdentifier());
        metadata.setSendersTimeStamp(inboundMetadata.getTimestamp());
        metadata.setReceivedTimeStamp(new Date());
        metadata.setTransmissionId(header.getIdentifier().getIdentifier());
        metadata.setInstanceType(header.getInstanceType().getType());
        metadata.setInstanceTypeVersion(header.getInstanceType().getVersion());
        metadata.setInstanceTypeStandard(header.getInstanceType().getStandard());

        return metadata;
    }

    // Setting metadata after sending the file from outbound..
    public static PeppolMessageMetadata create(@NotNull TransmissionResponse response) {
        Header header = response.getHeader();
        X509Certificate certificate = response.getEndpoint().getCertificate();
        X500Principal principal = certificate.getSubjectX500Principal();

        PeppolMessageMetadata metadata = new PeppolMessageMetadata();
        metadata.setMessageId(header.getIdentifier().getIdentifier());
        metadata.setRecipientId(header.getReceiver().getIdentifier());
        metadata.setSenderId(header.getSender().getIdentifier());
        metadata.setDocumentTypeIdentifier(header.getDocumentType().getIdentifier());
        metadata.setProfileTypeIdentifier(header.getProcess().getIdentifier());
        metadata.setSendingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setReceivingAccessPoint(principal.getName());
        metadata.setProtocol(response.getProtocol().getIdentifier());
        metadata.setSendersTimeStamp(response.getTimestamp());
        metadata.setReceivedTimeStamp(new Date());
        metadata.setTransmissionId(header.getIdentifier().getIdentifier());
        metadata.setInstanceType(header.getInstanceType().getType());
        metadata.setInstanceTypeVersion(header.getInstanceType().getVersion());
        metadata.setInstanceTypeStandard(header.getInstanceType().getStandard());

        return metadata;
    }

    // testing purposes
    public static PeppolMessageMetadata createDummy() {
        PeppolMessageMetadata metadata = new PeppolMessageMetadata();
        metadata.setMessageId("ff3bb2dc-3ff4-11e6-9605-97ed9690fe22");
        metadata.setTransmissionId("ff3bb2dc-3ff4-11e6-9605-97ed9690fe22");
        metadata.setRecipientId("9908:937789416");
        metadata.setSenderId("9908:937270062");
        metadata.setDocumentTypeIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1");
        metadata.setProfileTypeIdentifier("urn:www.cenbii.eu:profile:bii05:ver2.0");
        metadata.setSendingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setReceivingAccessPoint(OC_AP_COMMON_NAME);
        metadata.setProtocol("AS2");
        metadata.setSendersTimeStamp(new Date());
        metadata.setReceivedTimeStamp(new Date());
        metadata.setInstanceType("Invoice");
        metadata.setInstanceTypeVersion("2.1");
        metadata.setInstanceTypeStandard("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        return metadata;
    }
}