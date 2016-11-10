package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.route.TransportType;
import com.opuscapita.peppol.commons.container.status.ProcessingStatus;
import com.opuscapita.peppol.commons.validation.ValidationResult;
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
    private ProcessingStatus status;
    private ValidationResult validationResult;

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
    public ProcessingStatus getProcessingStatus() {
        if (status == null) {
            return new ProcessingStatus(TransportType.UNKNOWN, "", fileName);
        }
        return status;
    }

    @NotNull
    public ContainerMessage setStatus(@NotNull ProcessingStatus status) {
        this.status = status;
        return this;
    }

    @NotNull
    public ContainerMessage setStatus(@NotNull String status) {
        this.getProcessingStatus().setResult(status);
        return this;
    }

    @NotNull
    public ContainerMessage setStatus(@NotNull TransportType transportType, @NotNull String result) {
        this.getProcessingStatus().setTransportType(transportType).setResult(result);
        return this;
    }

    /**
     * Returns correlation ID if any or file name as correlation ID. Data is being read from processing status object.
     *
     * @return Returns correlation ID if any or file name as correlation ID
     */
    @NotNull
    public String getCorrelationId() {
        return getProcessingStatus().getCorrelationId();
    }

    @Override
    @NotNull
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

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
