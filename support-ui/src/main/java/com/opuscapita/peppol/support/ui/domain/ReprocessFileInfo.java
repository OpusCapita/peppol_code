package com.opuscapita.peppol.support.ui.domain;

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

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileInfo reprocessedFile;
    @Column(name = "ts")
    private Timestamp timestamp;

    @Override
    public int compareTo(ReprocessFileInfo reprocessFileInfo) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        if (this == reprocessFileInfo) {
            return EQUAL;
        }
        try {
            if (this.getTimestamp().after(reprocessFileInfo.getTimestamp())) {
                return BEFORE;
            }
            if (this.getTimestamp().before(reprocessFileInfo.getTimestamp())) {
                return AFTER;
            }
        } catch (NullPointerException pass) {
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
