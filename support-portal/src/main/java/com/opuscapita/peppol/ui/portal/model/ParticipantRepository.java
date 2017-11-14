package com.opuscapita.peppol.ui.portal.model;

import com.opuscapita.peppol.commons.revised_model.Participant;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, Integer> {
}
