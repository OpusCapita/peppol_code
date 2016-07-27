package com.opuscapita.peppol.events.persistance.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {
    public Message findBySenderAndInvoiceNumber(Customer sender, String invoiceNumber);
}
