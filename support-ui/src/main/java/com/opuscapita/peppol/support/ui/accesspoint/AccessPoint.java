package com.opuscapita.peppol.support.ui.accesspoint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by KACENAR1 on 22.12.2015
 */
@Entity
@DynamicUpdate
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "AccessPoint")
@Table(name = "access_points")
public class AccessPoint {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "ap_id")
    private String accessPointId;

    @Column(name = "ap_name")
    private String accessPointName;

    @Column(name = "emails")
    private String emailList;

    @Column(name = "contact_person")
    private String contactPerson;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(String accessPointId) {
        this.accessPointId = accessPointId;
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public String getEmailList() {
        return emailList;
    }

    public void setEmailList(String emailList) {
        this.emailList = emailList;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
