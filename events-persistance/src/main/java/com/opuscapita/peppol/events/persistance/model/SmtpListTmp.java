package com.opuscapita.peppol.events.persistance.model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "tmp_smtp_list")
public class SmtpListTmp {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "emails")
    private String emails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }
}
