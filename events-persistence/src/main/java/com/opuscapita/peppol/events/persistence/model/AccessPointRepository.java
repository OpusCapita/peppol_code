package com.opuscapita.peppol.events.persistence.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface AccessPointRepository extends JpaRepository<AccessPoint, Integer> {
    public AccessPoint findByAccessPointId(String accessPointId);
}
