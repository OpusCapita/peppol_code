package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Single process or module in execution route.
 *
 * @author Sergejs.Roze
 */
public class Process implements Serializable {
    private final String name;
    private final Endpoint endpoint;

    private final String ok;
    private final String nok;
    private final String error;

    private Status status = Status.NONE;

    public Process(@NotNull String name, @NotNull Endpoint endpoint, @Nullable String ok, @Nullable String nok, @Nullable String error) {
        this.name = name;
        this.endpoint = endpoint;
        this.ok = ok;
        this.nok = nok;
        this.error = error;
    }

    @NotNull
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Nullable
    public String getOk() {
        return ok;
    }

    @Nullable
    public String getNok() {
        return nok;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
