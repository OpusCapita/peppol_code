package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.events.persistence.model.util.TimeStampComparison;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

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
@Entity
@Table(name = "files")
public class FileInfo implements Comparable<FileInfo> {

    @Override
    public int compareTo(FileInfo fileInfo) {
        return TimeStampComparison.compare(this.getArrivedTimeStamp(), fileInfo.getArrivedTimeStamp());
    }

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @ManyToOne
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sentFile")
    @Sort(type = SortType.NATURAL)
    private SortedSet<SentFileInfo> sentInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "failedFile")
    @Sort(type = SortType.NATURAL)
    private SortedSet<FailedFileInfo> failedInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reprocessedFile")
    @Sort(type = SortType.NATURAL)
    private SortedSet<ReprocessFileInfo> reprocessInfo;

    public FileInfo() {
    }

    public FileInfo(File file) {
        this.filename = file.getName();
        this.fileSize = file.length();
        this.arrivedTimeStamp = new Timestamp(file.lastModified());
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
}
