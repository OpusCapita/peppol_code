package com.opuscapita.peppol.ui.portal.model;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface AttemptsRepository extends JpaRepository<Attempt, Long>, QueryDslPredicateExecutor<Attempt> {
}
