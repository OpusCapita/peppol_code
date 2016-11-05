package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.route.TransportType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds the whole data exchange bean inside the application.
 *
 * @author Sergejs.Roze
 */
public class ContainerMessage implements Serializable {
    private final Endpoint source;
    private final String sourceMetadata;

    private String fileName;

    private Route route;
    private BaseDocument document;
    private String transactionId;
    private TransportType transportType = TransportType.UNKNOWN;

    public ContainerMessage(@NotNull String metadata, @NotNull String fileName, @NotNull Endpoint source) {
        this.source = source;
        this.fileName = fileName;
        this.sourceMetadata = metadata;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @NotNull
    public ContainerMessage setFileName(@NotNull String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Nullable
    public Route getRoute() {
        return route;
    }

    @NotNull
    public ContainerMessage setRoute(@NotNull Route route) {
        this.route = route;
        return this;
    }

    @Nullable
    public BaseDocument getBaseDocument() {
        return document;
    }

    @NotNull
    public ContainerMessage setBaseDocument(@NotNull BaseDocument baseDocument) {
        this.document = baseDocument;
        return this;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    /**
     * Returns customer ID depending on the direction of the message, either sender or recipient ID.
     */
    @Nullable
    public String getCustomerId() {
        if (getBaseDocument() == null) {
            return null;
        }
        return isInbound() ? getBaseDocument().getRecipientId() : getBaseDocument().getSenderId();
    }

    @NotNull
    public String getSourceMetadata() {
        return sourceMetadata;
    }

    @NotNull
    public Endpoint getSource() {
        return source;
    }

    public byte[] getBytes() {
        return new Gson().toJson(this).getBytes();
    }

    @Nullable
    public String getTransactionId() {
        return transactionId;
    }

    @NotNull
    public ContainerMessage setTransactionId(@NotNull String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @NotNull
    public TransportType getTransportType() {
        return transportType;
    }

    @NotNull
    public ContainerMessage setTransportType(TransportType transportType) {
        this.transportType = transportType;
        return this;
    }

    @Override
    public String toString() {
        String result = fileName + " from " + source;
        if (document != null) {
            result += " [" + document.getClass().getName() + "]";
        }
        if (route != null) {
            result += ":" + route;
        }

        return result;
    }

}
