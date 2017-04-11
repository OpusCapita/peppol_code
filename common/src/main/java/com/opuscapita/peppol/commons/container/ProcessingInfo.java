package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds data related to the processing excluding the document data.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class ProcessingInfo implements Serializable {
    private final Endpoint source;
    private String sourceMetadata;
    private Route route;
    private String transactionId;
    private ValidationResult validationResult;
    private String correlationId;
    private Endpoint currentEndpoint;
    private String currentStatus;
    private Exception processingException;

    /**
     * @param source the initial endpoint
     * @param sourceMetadata any voluntary data provided by the original endpoint
     */
    public ProcessingInfo(@NotNull Endpoint source, @NotNull String sourceMetadata) {
        this.source = source;
        this.sourceMetadata = sourceMetadata;
    }

    @Nullable
    public Route getRoute() {
        return route;
    }

    public ProcessingInfo setRoute(@NotNull Route route) {
        this.route = route;
        return this;
    }

    public boolean isInbound() {
        return source.isInbound();
    }

    public Endpoint getSource() {
        return source;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public ProcessingInfo setCurrentStatus(@NotNull Endpoint endpoint, @NotNull String status) {
        this.currentStatus = status;
        this.currentEndpoint = endpoint;
        return this;
    }

    public void setValidationResult(@NotNull ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    @Nullable
    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(@Nullable Exception processingException) {
        this.processingException = processingException;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(@NotNull String transactionId) {
        this.transactionId = transactionId;
    }

    @NotNull
    public String getSourceMetadata() {
        return sourceMetadata;
    }

    public void setSourceMetadata(@NotNull String sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public Endpoint getCurrentEndpoint() {
        return currentEndpoint;
    }
}
