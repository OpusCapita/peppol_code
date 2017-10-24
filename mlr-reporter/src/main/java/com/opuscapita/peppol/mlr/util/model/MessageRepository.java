package com.opuscapita.peppol.mlr.util.model;

import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by bambr on 16.28.9.
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {
    Message findBySenderAndInvoiceNumber(Customer customer, String invoiceId);
}
