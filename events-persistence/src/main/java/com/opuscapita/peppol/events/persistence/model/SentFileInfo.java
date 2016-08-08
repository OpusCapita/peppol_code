package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.events.persistence.model.util.TimeStampComparison;

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

    @Override
    public int compareTo(SentFileInfo sentFileInfo) {
        return TimeStampComparison.compare(this.getTimestamp(), sentFileInfo.getTimestamp());
    }

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
