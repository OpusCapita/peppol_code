package com.opuscapita.peppol.eventing.revised.repositories;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import org.springframework.data.repository.CrudRepository;

public interface AttemptsRepository extends CrudRepository<Attempt, Long> {
}
