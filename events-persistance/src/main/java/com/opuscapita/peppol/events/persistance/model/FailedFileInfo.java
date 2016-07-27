package com.opuscapita.peppol.events.persistance.model;


import com.opuscapita.peppol.events.persistance.model.util.TimeStampComparison;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.20.11
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "failed")
public class FailedFileInfo implements Comparable<FailedFileInfo> {

    @Override
    public int compareTo(FailedFileInfo failedFileInfo) {
        return TimeStampComparison.compare(this.getTimestamp(), failedFileInfo.getTimestamp());
    }

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileInfo failedFile;

    @Column(name = "ts")
    private Timestamp timestamp;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "invalid")
    private boolean invalid;

    @Column(name = "error_file_path")
    private String errorFilePath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileInfo getFailedFile() {
        return failedFile;
    }

    public void setFailedFile(FileInfo failedFile) {
        this.failedFile = failedFile;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public void setErrorFilePath(String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }
}
