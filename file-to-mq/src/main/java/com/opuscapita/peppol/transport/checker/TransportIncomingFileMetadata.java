package com.opuscapita.peppol.transport.checker;

import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.transmission.TransmissionResult;
import no.difi.vefa.peppol.common.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;


public class TransportIncomingFileMetadata implements TransmissionResult {

    private final TransmissionIdentifier transmissionIdentifier;

    private final Header header;

    private final Date timestamp;

    private final TransportProfile transportProfile;

    public TransportIncomingFileMetadata(@NotNull Header header) {
        this.header = header;
        this.timestamp = new Date();
        this.transportProfile = TransportProfile.of(DocumentTypeIdentifier.DEFAULT_SCHEME.getIdentifier());
        this.transmissionIdentifier = TransmissionIdentifier.generateUUID();
    }

    public TransmissionIdentifier getTransmissionIdentifier() {
        return transmissionIdentifier;
    }

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public TransportProfile getProtocol() {
        return transportProfile;
    }

    @Override
    public TransportProtocol getTransportProtocol() {
        return TransportProtocol.AS2;
    }

    @Override
    public Digest getDigest() {
        return null;
    }

    @Override
    public List<Receipt> getReceipts() {
        return null;
    }

    @Override
    public Receipt primaryReceipt() {
        return null;
    }
}