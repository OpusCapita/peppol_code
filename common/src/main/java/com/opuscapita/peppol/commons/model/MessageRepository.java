package com.opuscapita.peppol.commons.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Daniil on 13.07.2016.
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {
    Message findBySenderAndInvoiceNumber(Customer sender, String invoiceNumber);
}
