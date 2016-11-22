package com.opuscapita.peppol.commons.container.status;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Sergejs.Roze
 */
public class ProcessingStatus implements Serializable {
    private String componentName;
    private String result;
    private final String correlationId;

    public ProcessingStatus(@NotNull String componentName, @NotNull String result, @NotNull String correlationId) {
        this.componentName = componentName;
        this.result = result;
        this.correlationId = correlationId;
    }

    @NotNull
    public String getComponentName() {
        return componentName;
    }

    @NotNull
    public ProcessingStatus setComponentName(@NotNull String componentName) {
        this.componentName = componentName;
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
