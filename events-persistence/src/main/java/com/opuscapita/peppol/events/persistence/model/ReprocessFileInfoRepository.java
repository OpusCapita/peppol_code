package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.commons.model.ReprocessFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by bambr on 16.12.9.
 */
public interface ReprocessFileInfoRepository extends JpaRepository<ReprocessFileInfo, Integer> {
}
