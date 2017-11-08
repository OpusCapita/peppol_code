package com.opuscapita.peppol.ui.portal.model;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptsRepository extends JpaRepository<Attempt, Long> {
}
