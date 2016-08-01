package com.opuscapita.peppol.events.persistence.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public Customer findByIdentifier(String customerId);
}
