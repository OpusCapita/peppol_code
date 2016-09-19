package com.opuscapita.peppol.commons.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByIdentifier(String customerId);
}
