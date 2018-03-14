package com.opuscapita.peppol.commons.events;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.SortedSet;

public class Attempt implements Comparable<Attempt>, Serializable {
    private final String id;
    private final SortedSet<Event> events;
    private String filename;

    public Attempt(String id, SortedSet<Event> events, String filename) {
        this.id = id;
        this.events = events;
        this.filename = filename;
    }

    public String getId() {
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

    @Override
    public int compareTo(@NotNull Attempt other) {
        int res = Long.compare(
                Long.valueOf(StringUtils.left(id, 13)),
                Long.valueOf(StringUtils.left(other.getId(), 13)));
        return res != 0 ? res : id.compareTo(other.getId());
    }

    public void setFileName(String fileName) {
        this.filename = fileName;
    }
}
