package com.opuscapita.peppol.commons.revised_model;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.SortedSet;

@Entity
@Table(name = "attempts")
public class Attempt implements Comparable<Attempt> {

    @Id
    private String id; //Unix timestamp to be stored here

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    @ElementCollection(targetClass = Event.class)
    private SortedSet<Event> events;

    @Column(name = "filename")
    private String filename;

    @ManyToOne(cascade = CascadeType.ALL)
    private Message message;

    public Attempt() {
    }

    public Attempt(String id, SortedSet<Event> events, String filename, Message message) {
        this.id = id;
        this.events = events;
        this.filename = filename;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SortedSet<Event> getEvents() {
        return events;
    }

    public void setEvents(SortedSet<Event> events) {
        this.events = events;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Attempt{" +
                "id=" + id +
                ", events=" + events +
                ", filename='" + filename + '\'' +
                ", message=" + message.getId() +
                '}';
    }

    @Override
    public int compareTo(@NotNull Attempt other) {
        int part1 = getMessage().compareTo(other.getMessage());
        int part2 = Integer.compare(getId().hashCode(), other.getId().hashCode());
        if(part1 != 0) {
            return part1;
        }
        if(part2 != 0) {
            return part2;
        }
        return 0;
    }
}
