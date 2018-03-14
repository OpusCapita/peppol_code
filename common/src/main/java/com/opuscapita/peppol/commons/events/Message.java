package com.opuscapita.peppol.commons.events;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.SortedSet;

public class Message implements Serializable {
    private final String id;
    private final long created;
    private final boolean isInbound;
    private final SortedSet<Attempt> attempts;
    private String documentType;
    private String documentNumber;
    private String documentDate;
    private String dueDate;

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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", isInbound=" + isInbound +
                ", attempts=" + attempts +
                ", documentType=" + documentType +
                ", documentNumber=" + documentNumber +
                ", documentDate=" + documentDate +
                ", dueDate=" + dueDate +
                '}';
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
