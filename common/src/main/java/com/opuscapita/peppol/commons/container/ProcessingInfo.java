package com.opuscapita.peppol.commons.container;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.document.ApInfo;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.events.Message;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds data related to the processing excluding the document data.
 */
public class ProcessingInfo implements Serializable {

    private static final long serialVersionUID = -556566093311452295L;

    @Since(1.0) private Endpoint source;
    @Since(1.0) private Route route;
    @Since(1.0) private Endpoint currentEndpoint;
    @Since(1.0) private String currentStatus;
    @Since(1.0) private String processingException;
    @Since(1.2) private String originalSource;
    @Since(1.2) private Message eventingMessage;
    @Since(1.5) private PeppolMessageMetadata peppolMessageMetadata;

    public ProcessingInfo(@NotNull Endpoint source) {
        this.source = source;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ProcessingInfo setCurrentStatus(@NotNull Endpoint endpoint, @NotNull String status) {
        this.currentEndpoint = endpoint;
        this.currentStatus = status;
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
        PeppolMessageMetadata metadata = getPeppolMessageMetadata();
        if (metadata == null) {
            return null;
        }
        return metadata.getTransmissionId();
    }

    public Endpoint getCurrentEndpoint() {
        return currentEndpoint == null ? source : currentEndpoint;
    }

    /**
     * returns sending AP info for inbound and receiving AP info for outbound
     */
    public ApInfo getApInfo() {
        PeppolMessageMetadata metadata = getPeppolMessageMetadata();
        if (metadata == null) {
            return null;
        }
        return ApInfo.parseFromCommonName(isInbound() ? metadata.getSendingAccessPoint() : metadata.getReceivingAccessPoint());
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

    public Message getEventingMessage() {
        return eventingMessage;
    }

    public void setEventingMessage(Message eventingMessage) {
        this.eventingMessage = eventingMessage;
    }


    public PeppolMessageMetadata getPeppolMessageMetadata() {
        return peppolMessageMetadata;
    }

    public void setPeppolMessageMetadata(PeppolMessageMetadata peppolMessageMetadata) {
        this.peppolMessageMetadata = peppolMessageMetadata;
    }
}
