package com.opuscapita.peppol.commons.revised_model;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "peppol_messages")
public class Message implements  Comparable<Message> {
    @Id
    private String id;  //Use EventingMessageUtil.generateMessageId to fill this field

    @Column(name = "created")
    private long created;

    @Column(name = "inbound")
    private boolean inbound;

    @Column(name = "sender")
    private String sender;

    @Column(name = "recipient")
    private String recipient;


    @Column
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    @ElementCollection(targetClass = Attempt.class)
    private SortedSet<Attempt> attempts;

    public Message() {
    }

    public Message(String id, long created, boolean inbound, SortedSet<Attempt> attempts) {
        this.id = id;
        this.created = created;
        this.inbound = inbound;
        this.attempts = attempts;
    }

    public Message(String id, long created, boolean inbound, String sender, String recipient, SortedSet<Attempt> attempts) {
        this.id = id;
        this.created = created;
        this.inbound = inbound;
        this.sender = sender;
        this.recipient = recipient;
        this.attempts = attempts;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    public boolean isInbound() {
        return inbound;
    }

    public void setInbound(boolean inbound) {
        this.inbound = inbound;
    }

    public String getLastStatus(){
        List<Event> list = attempts.stream().flatMap(attempt -> attempt.getEvents().stream()).collect(Collectors.toList());
        return list.size() > 0 ? list.get(list.size() - 1).getStatus() : "N\\A";
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", inbound=" + inbound +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", attempts=" + attempts +
                '}';
    }

    @Override
    public int compareTo(@NotNull Message other) {
        return Integer.compare(getId().hashCode(), other.getId().hashCode());
    }
}
