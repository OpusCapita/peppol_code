package com.opuscapita.peppol.commons.revised_model;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.SortedSet;

@Entity
@Table(name = "peppol_messages")
public class Message {
    @Id
    private String id;  //Use EventingMessageUtil.generateMessageId to fill this field

    @Column(name = "created")
    private long created;

    @Column
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    @ElementCollection(targetClass = Attempt.class)
    private SortedSet<Attempt> attempts;

    public Message() {
    }

    public Message(String id, long created, SortedSet<Attempt> attempts) {
        this.id = id;
        this.created = created;
        this.attempts = attempts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public SortedSet<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(SortedSet<Attempt> attempts) {
        this.attempts = attempts;
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
