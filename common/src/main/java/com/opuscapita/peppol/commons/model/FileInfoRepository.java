package com.opuscapita.peppol.commons.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    FileInfo findByFilename(String filename);
}
