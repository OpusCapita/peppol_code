package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.commons.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by bambr on 16.28.9.
 */
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    FileInfo findByFilename(String fileName);
}
