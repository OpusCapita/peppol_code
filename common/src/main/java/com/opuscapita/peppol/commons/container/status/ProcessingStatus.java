package com.opuscapita.peppol.commons.container.status;

import com.opuscapita.peppol.commons.container.route.TransportType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Sergejs.Roze
 */
public class ProcessingStatus implements Serializable {
    private TransportType transportType;
    private String result;
    private final String correlationId;

    public ProcessingStatus(@NotNull TransportType transportType, @NotNull String result, @NotNull String correlationId) {
        this.transportType = transportType;
        this.result = result;
        this.correlationId = correlationId;
    }

    @NotNull
    public TransportType getTransportType() {
        return transportType;
    }

    @NotNull
    public ProcessingStatus setTransportType(@NotNull TransportType transportType) {
        this.transportType = transportType;
        return this;
    }

    @NotNull
    public String getResult() {
        return result;
    }

    @NotNull
    public ProcessingStatus setResult(@NotNull String result) {
        this.result = result;
        return this;
    }

    @NotNull
    public String getCorrelationId() {
        return correlationId;
    }

}
