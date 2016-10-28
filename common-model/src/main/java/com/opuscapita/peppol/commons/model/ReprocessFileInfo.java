package com.opuscapita.peppol.commons.model;

import com.opuscapita.peppol.commons.model.util.TimeStampComparison;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "reprocesses")
public class ReprocessFileInfo implements Comparable<ReprocessFileInfo> {

    @Override
    public int compareTo(ReprocessFileInfo reprocessFileInfo) {
        return TimeStampComparison.compare(this.getTimestamp(), reprocessFileInfo.getTimestamp());
    }

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileInfo reprocessedFile;

    @Column(name = "ts")
    private Timestamp timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FileInfo getReprocessedFile() {
        return reprocessedFile;
    }

    public void setReprocessedFile(FileInfo reprocessedFile) {
        this.reprocessedFile = reprocessedFile;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
