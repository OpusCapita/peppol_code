package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The complete route from end-to-end.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("unused")
public class Route {
    private List<Endpoint> endpoints = new ArrayList<>();
    private String description;
    private String mask;
    private Endpoint source;

    private String status = "";
    private int current = 0;

    @Nullable
    public Endpoint pop() {
        return pop(null);
    }

    /**
     * Returns next process in the route and makes next process current.
     *
     * @param status the outcome of the finished process, optional
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public Endpoint pop(@Nullable String status) {
        if (current >= endpoints.size()) {
            return null;
        }
        Endpoint e = endpoints.get(current);
        this.status += status + "\n";

        current++;
        return e;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String result = description;
        if (mask != null) {
            result += " (" + mask + ") ";
        }
        result += "[ " + source + " ";
        for (Endpoint endpoint : endpoints) {
            result += endpoint + " ";
        }
        return result + "]";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public Endpoint getSource() {
        return source;
    }

    public void setSource(Endpoint source) {
        this.source = source;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
