package com.opuscapita.peppol.commons.revised_model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DynamicUpdate
@Table(name = "senders")
public class Customer {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "customer_id")
    private String identifier;

    @Column(name = "customer_name", nullable = false)
    private String name = "n/a";

    @Column(name = "outbound_emails")
    private String outboundEmails;

    @Column(name = "inbound_emails")
    private String inboundEmails;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "os_contact_person")
    private String responsiblePerson;

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
        if (name != null) {
            this.name = name;
        }
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

    public String getInboundEmails() {
        return inboundEmails;
    }

    public void setInboundEmails(String inboundEmails) {
        this.inboundEmails = inboundEmails;
    }
}
