package com.opuscapita.peppol.commons.events;

import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

public class Message {
    private final String id;
    private final long created;
    private final boolean isInbound;
    private final SortedSet<Attempt> attempts;

    public Message(@NotNull String id, long created, boolean isInbound, SortedSet<Attempt> attempts) {
        this.id = id;
        this.created = created;
        this.isInbound = isInbound;
        this.attempts = attempts;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public boolean isInbound() {
        return isInbound;
    }

    public SortedSet<Attempt> getAttempts() {
        return attempts;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", isInbound=" + isInbound +
                ", attempts=" + attempts +
                '}';
    }
}
