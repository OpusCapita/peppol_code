package com.opuscapita.peppol.commons.container;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.events.Message;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds data related to the processing excluding the document data.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ProcessingInfo implements Serializable {
    private static final long serialVersionUID = -556566093311452295L;

    @Since(1.0)
    private final Endpoint source;
    @Since(1.0)
    private String sourceMetadata;
    @Since(1.0)
    private Route route;
    @Since(1.0)
    private String transactionId;
    @Since(1.0)
    private String correlationId;
    @Since(1.0)
    private Endpoint currentEndpoint;
    @Since(1.0)
    private String currentStatus;
    @Since(1.0)
    private String processingException; // this is actually an exception message but called exception for historical reasons
    @Since(1.0)
    private String commonName;
    @Since(1.0)
    private String sendingProtocol;
    @Since(1.1)
    private String originalSource;
    private String messageId;
    private long attemptId;
    private Message eventingMessage;

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
    public String getProcessingException() {
        return processingException;
    }

    public void setProcessingException(@Nullable String processingException) {
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

    public void setSourceMetadata(@NotNull String sourceMetadata) {
        this.sourceMetadata = sourceMetadata;
    }

    public Endpoint getCurrentEndpoint() {
        return currentEndpoint;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setSendingProtocol(String sendingProtocol) {
        this.sendingProtocol = sendingProtocol;
    }

    public String getSendingProtocol() {
        return sendingProtocol;
    }

    @NotNull
    public String getOriginalSource() {
        if (StringUtils.isBlank(originalSource)) {
            return source.getName();
        }
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setAttemptId(long attemptId) {
        this.attemptId = attemptId;
    }

    public long getAttemptId() {
        return attemptId;
    }

    public void setEventingMessage(Message eventingMessage) {
        this.eventingMessage = eventingMessage;
    }

    public Message getEventingMessage() {
        return eventingMessage;
    }
}
