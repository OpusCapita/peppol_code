package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The complete route from end-to-end.
 *
 * @author Sergejs.Roze
 */
public class Route {
    private final Process source;
    private final List<Process> processes;

    private int index = 0;

    public Route(@NotNull Process source, @NotNull List<Process> processes) {
        this.source = source;
        this.processes = processes;
    }

    /**
     * Returns next process in the route and makes next process current.
     *
     * @param status the outcome of the finished process
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public Process pop(@NotNull String status) {
        if (index >= processes.size()) {
            return null;
        }
        Process current = processes.get(index);
        current.addStatus(status);

        index++;
        return index >= processes.size() ? null : processes.get(index);
    }

    public void addStatus(@NotNull String status) {
        if (index < processes.size()) {
            processes.get(index).addStatus(status);
        }
    }

    @NotNull
    public Process getSource() {
        return source;
    }

    public boolean isInbound() {
        return source.getEndpoint() == Endpoint.PEPPOL;
    }
}
