package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.Route;
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
    private static final long serialVersionUID = -556566093311452294L;

    private final Endpoint source;
    private String sourceMetadata;
    private Route route;
    private String transactionId;
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

    public ProcessingInfo setRoute(@Nullable Route route) {
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

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ProcessingInfo setCurrentStatus(@NotNull Endpoint endpoint, @NotNull String status) {
        this.currentStatus = status;
        this.currentEndpoint = endpoint;
        return this;
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
        if (this.correlationId == null) {
            setCorrelationId(transactionId);
        }
    }

    @NotNull
    public String getSourceMetadata() {
        return sourceMetadata;
    }

    @SuppressWarnings("unused")
    public void setSourceMetadata(@NotNull String sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public Endpoint getCurrentEndpoint() {
        return currentEndpoint;
    }
}
