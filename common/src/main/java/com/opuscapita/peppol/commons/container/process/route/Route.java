package com.opuscapita.peppol.commons.container.process.route;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The complete route from end-to-end.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("unused")
public class Route implements Serializable {
    private static final long serialVersionUID = -9123055794300438133L;

    private List<String> endpoints = new ArrayList<>();
    private String description = "";
    private String mask = "";
    private String source = "";

    private int current = 0;

    public Route() {}

    /**
     * Copy constructor.
     *
     * @param other the other route to be used as an example
     */
    public Route(@NotNull Route other) {
        this.endpoints = other.getEndpoints();
        this.description = other.getDescription();
        this.mask = other.getMask();
        this.source = other.getSource();
    }

    /**
     * Returns next process in the route and makes next process current.
     *
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public String pop() {
        if (current >= endpoints.size()) {
            return null;
        }
        return endpoints.get(current++);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(description);
        if (mask != null) {
            result.append(" (").append(mask).append(") ");
        }
        result.append("[ ").append(source).append(" ");
        for (String endpoint : endpoints) {
            result.append(endpoint).append(" ");
        }
        return result + "]";
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getMask() {
        return mask;
    }

    @SuppressWarnings("SameParameterValue")
    public void setMask(@Nullable String mask) {
        this.mask = mask;
    }

    @NotNull
    public String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(@NotNull List<String> endpoints) {
        this.endpoints = endpoints;
    }

}
