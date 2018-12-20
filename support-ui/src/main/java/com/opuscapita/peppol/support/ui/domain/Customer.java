package com.opuscapita.peppol.support.ui.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@DynamicUpdate
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "senders")
public class Customer {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "customer_id")
    private String identifier;

    @Column(name = "customer_name")
    private String name;

    @Column(name = "outbound_emails")
    private String outboundEmails;

    @Column(name = "inbound_emails")
    private String inboundEmails;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "os_contact_person")
    private String responsiblePerson;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private Set<Message> messages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutboundEmails() {
        return outboundEmails;
    }

    public void setOutboundEmails(String emailList) {
        this.outboundEmails = emailList;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public String getInboundEmails() {
        return inboundEmails;
    }

    public void setInboundEmails(String inboundEmails) {
        this.inboundEmails = inboundEmails;
    }
}
