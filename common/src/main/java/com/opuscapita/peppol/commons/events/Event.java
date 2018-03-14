package com.opuscapita.peppol.commons.events;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Event implements Comparable<Event>, Serializable {
    private final long id;
    private final String source;
    private final String status;
    private final boolean terminal;
    private final String details;

    public Event(long id, String source, String status, boolean terminal, String details) {
        this.id = id;
        this.source = source;
        this.status = status;
        this.terminal = terminal;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getStatus() {
        return status;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", terminal=" + terminal +
                ", details='" + details + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull Event o) {
        return Long.compare(id, o.id);
    }
}
