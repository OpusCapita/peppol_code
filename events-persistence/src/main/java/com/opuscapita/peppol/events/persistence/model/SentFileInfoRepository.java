package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.commons.model.SentFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by bambr on 16.12.9.
 */
public interface SentFileInfoRepository extends JpaRepository<SentFileInfo, Integer> {
    List<SentFileInfo> findBySentFileIdAndTransmissionId(Integer sentFileId, String transmissionId);
}
