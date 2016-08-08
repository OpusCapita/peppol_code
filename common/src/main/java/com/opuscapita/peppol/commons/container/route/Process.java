package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Properties;

/**
 * Single process or module in execution route.
 *
 * @author Sergejs.Roze
 */
public class Process implements Serializable {
    private final Endpoint endpoint;
    private final Properties properties;
    private String status = "";

    public Process(@NotNull Endpoint endpoint, @Nullable Properties properties) {
        this.endpoint = endpoint;
        this.properties = properties == null ? new Properties() : properties;
    }

    @NotNull
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public String getStatus() {
        return status;
    }

    public void addStatus(@NotNull String status) {
        this.status += status;
    }

    @NotNull
    public Properties getProperties() {
        return properties;
    }
}
