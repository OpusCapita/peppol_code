package com.opuscapita.peppol.commons.container.status;

import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Sergejs.Roze
 */
public class ProcessingStatus implements Serializable {
    private final String correlationId;
    private Endpoint endpoint;
    private String result;

    public ProcessingStatus(@NotNull Endpoint endpoint, @NotNull String result, @NotNull String correlationId) {
        this.endpoint = endpoint;
        this.result = result;
        this.correlationId = correlationId;
    }

    @NotNull
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @NotNull
    public ProcessingStatus withEndpoint(@NotNull Endpoint endpoint) {
        this.endpoint = endpoint;
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

    @Override
    public String toString() {
        return "Endpoint = " + endpoint + ", status = " + result;
    }
}
