package com.opuscapita.peppol.commons.model;

import com.opuscapita.peppol.commons.model.util.TimeStampComparison;
import org.hibernate.annotations.SortNatural;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.File;
import java.sql.Timestamp;
import java.util.SortedSet;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.2.12
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Entity
@Table(name = "files")
public class FileInfo implements Comparable<FileInfo> {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id")
    private Message message;
    @Column(name = "filename")
    private String filename;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "arrived_ts")
    private Timestamp arrivedTimeStamp;
    @Column(name = "duplicate")
    private boolean duplicate;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sentFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural
    private SortedSet<SentFileInfo> sentInfo;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "failedFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural
    private SortedSet<FailedFileInfo> failedInfo;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "reprocessedFile", cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural
    private SortedSet<ReprocessFileInfo> reprocessInfo;
//    @OneToOne(fetch = FetchType.EAGER, mappedBy = "relatedFile", cascade = CascadeType.ALL, orphanRemoval = true)
//    private EmailInfo emailInfo;

    public FileInfo() {
    }

    public FileInfo(File file) {
        this.filename = file.getName();
        this.fileSize = file.length();
        this.arrivedTimeStamp = new Timestamp(file.lastModified());
    }

    @Override
    public int compareTo(@NotNull FileInfo fileInfo) {
        return TimeStampComparison.compare(this.getArrivedTimeStamp(), fileInfo.getArrivedTimeStamp());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Timestamp getArrivedTimeStamp() {
        return arrivedTimeStamp;
    }

    public void setArrivedTimeStamp(Timestamp arrivedTimeStamp) {
        this.arrivedTimeStamp = arrivedTimeStamp;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public SortedSet<SentFileInfo> getSentInfo() {
        return sentInfo;
    }

    public void setSentInfo(SortedSet<SentFileInfo> sentInfo) {
        this.sentInfo = sentInfo;
    }

    public SortedSet<FailedFileInfo> getFailedInfo() {
        return failedInfo;
    }

    public void setFailedInfo(SortedSet<FailedFileInfo> failedInfo) {
        this.failedInfo = failedInfo;
    }

    public SortedSet<ReprocessFileInfo> getReprocessInfo() {
        return reprocessInfo;
    }

    public void setReprocessInfo(SortedSet<ReprocessFileInfo> reprocessInfo) {
        this.reprocessInfo = reprocessInfo;
    }

//    public EmailInfo getEmailInfo() {
//        return emailInfo;
//    }
//
//    public void setEmailInfo(EmailInfo emailInfo) {
//        this.emailInfo = emailInfo;
//    }
}
