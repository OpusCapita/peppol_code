package com.opuscapita.peppol.support.ui.domain;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sent")
public class SentFileInfo implements Comparable<SentFileInfo> {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileInfo sentFile;
    @Column(name = "ts")
    private Timestamp timestamp;
    @Column(name = "forced")
    private boolean forced;
    @Column(name = "transmission_id")
    private String transmissionId;
    @Column(name = "ap_id")
    private String apId;
    @Column(name = "ap_company_name")
    private String apCompanyName;
    @Column(name = "ap_protocol")
    private String apProtocol;

    @Override
    public int compareTo(SentFileInfo sentFileInfo) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == sentFileInfo) {
            return EQUAL;
        }
        try {
            if (this.getTimestamp().after(sentFileInfo.getTimestamp())) {
                return BEFORE;
            }
            if (this.getTimestamp().before(sentFileInfo.getTimestamp())) {
                return AFTER;
            }
        } catch (NullPointerException e) {
            return AFTER;
        }

        return EQUAL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileInfo getSentFile() {
        return sentFile;
    }

    public void setSentFile(FileInfo sentFile) {
        this.sentFile = sentFile;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public String getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(String transmissionId) {
        this.transmissionId = transmissionId;
    }

    public String getApId() {
        return apId;
    }

    public void setApId(String apId) {
        this.apId = apId;
    }

    public String getApCompanyName() {
        return apCompanyName;
    }

    public void setApCompanyName(String apCompanyName) {
        this.apCompanyName = apCompanyName;
    }

    public String getApProtocol() {
        return apProtocol;
    }

    public void setApProtocol(String apProtocol) {
        this.apProtocol = apProtocol;
    }
}
