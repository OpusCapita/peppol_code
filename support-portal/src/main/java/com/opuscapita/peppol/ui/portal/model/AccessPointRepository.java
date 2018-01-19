package com.opuscapita.peppol.ui.portal.model;


import com.opuscapita.peppol.commons.revised_model.AccessPoint;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface AccessPointRepository extends CrudRepository<AccessPoint, Integer>, QueryDslPredicateExecutor<AccessPoint> {
}
