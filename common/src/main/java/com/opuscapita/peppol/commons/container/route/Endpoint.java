package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Single endpoint of the module/process/whatever in a whole route.
 * Each endpoint has own name (service name) and type.
 *
 * @author Sergejs.Roze
 */
public class Endpoint implements Serializable {
    public enum Type {
        PEPPOL,
        GATEWAY,
        REST,
        REPROCESS,
        RETRY,
        TEST,
        QUEUE
    }

    private final String name;
    private final Type type;

    /**
     * Creates new endpoint information.
     *
     * @param name the name of the service
     * @param type the logical type of the service
     */
    public Endpoint(@NotNull String name, @NotNull Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isInbound() {
        return type == Type.PEPPOL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endpoint)) return false;

        Endpoint endpoint = (Endpoint) o;

        return name.equals(endpoint.name) && type == endpoint.type;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
