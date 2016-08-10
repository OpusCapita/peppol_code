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
public class Route {
    private final List<Endpoint> processes;
    private String status = "";

    private int index = 0;

    public Route(@NotNull List<Endpoint> processes) {
        this.processes = processes;
    }

    public Route(@NotNull Endpoint source, @NotNull String configLine) {
        processes = new ArrayList<>();
        processes.add(source);

        String[] tokens = configLine.split(",");
        if (tokens.length <= 1) {
            return;
        }

        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i].trim();
            boolean found = false;
            for (Endpoint e : Endpoint.values()) {
                if (e.name().equalsIgnoreCase(token)) {
                    processes.add(e);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Cannot find endpoint named " + token); // TODO proper error handling
            }
        }
    }

    /**
     * Returns next process in the route and makes next process current.
     *
     * @param status the outcome of the finished process
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public Endpoint pop(@NotNull String status) {
        if (index >= processes.size()) {
            return null;
        }
        Endpoint current = processes.get(index);
        this.status += status + "\n";

        index++;
        return index >= processes.size() ? null : processes.get(index);
    }

    public boolean isInbound() {
        return processes.size() != 0 && processes.get(0) == Endpoint.PEPPOL;
    }

    @Override
    public String toString() {
        String result = "";
        result += processes.stream().map(Object::toString).reduce((a, b) -> a + " " + b);
        return result;
    }

    @NotNull
    public String getStatus() {
        return status;
    }
}
