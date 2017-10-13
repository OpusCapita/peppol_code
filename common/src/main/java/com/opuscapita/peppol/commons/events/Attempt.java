package com.opuscapita.peppol.commons.events;

import java.util.SortedSet;

public class Attempt {
    private final long id;
    private final SortedSet<Event> events;
    private final String filename;

    public Attempt(long id, SortedSet<Event> events, String filename) {
        this.id = id;
        this.events = events;
        this.filename = filename;
    }

    public long getId() {
        return id;
    }

    public SortedSet<Event> getEvents() {
        return events;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "Attempt{" +
                "id=" + id +
                ", events=" + events +
                ", filename='" + filename + '\'' +
                '}';
    }
}
