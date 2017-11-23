package com.opuscapita.peppol.commons.revised_model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "peppol_message_events")
public class Event implements Comparable<Event> {
    @Id
    private long id; //Unix timestamp to be stored here

    @Column
    private String source;

    @Column
    private String status;

    @Column
    private boolean terminal;

    @Column(length = 65535, columnDefinition = "text")
    private String details;

    @ManyToOne(cascade = CascadeType.ALL)
    private Attempt attempt;

    public Event(long id, String source, String status, boolean terminal, String details, Attempt attempt) {
        this.id = id;
        this.source = source;
        this.status = status;
        this.terminal = terminal;
        this.details = details;
        this.attempt = attempt;
    }

    public Event() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public String getDetails() {
        return details;
    }

    public Attempt getAttempt() {
        return attempt;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", terminal=" + terminal +
                ", details='" + details + '\'' +
                ", attempt=" + attempt.getId() +
                '}';
    }

    @Override
    public int compareTo(@NotNull Event other) {
        return Long.compare(id, other.getId());
    }
}
