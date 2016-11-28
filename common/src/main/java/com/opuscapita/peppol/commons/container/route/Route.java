package com.opuscapita.peppol.commons.container.route;

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
    private List<String> endpoints = new ArrayList<>();
    private String description;
    private String mask;
    private Endpoint source;

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
        String e = endpoints.get(current);

        current++;
        return e;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    @Override
    public String toString() {
        String result = description;
        if (mask != null) {
            result += " (" + mask + ") ";
        }
        result += "[ " + source + " ";
        for (String endpoint : endpoints) {
            result += endpoint + " ";
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

    public void setMask(@Nullable String mask) {
        this.mask = mask;
    }

    @NotNull
    public Endpoint getSource() {
        return source;
    }

    public void setSource(@NotNull Endpoint source) {
        this.source = source;
    }

    @NotNull
    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(@NotNull List<String> endpoints) {
        this.endpoints = endpoints;
    }
}
