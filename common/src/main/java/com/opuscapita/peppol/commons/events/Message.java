package com.opuscapita.peppol.commons.events;

import java.util.SortedSet;

public class Message {
    private final String id;
    private final long created;
    private final SortedSet<Attempt> attempts;

    public Message(String id, long created, SortedSet<Attempt> attempts) {
        this.id = id;
        this.created = created;
        this.attempts = attempts;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public SortedSet<Attempt> getAttempts() {
        return attempts;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", attempts=" + attempts +
                '}';
    }
}
