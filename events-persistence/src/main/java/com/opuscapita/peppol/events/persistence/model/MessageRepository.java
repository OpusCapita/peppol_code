package com.opuscapita.peppol.events.persistence.model;

import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by bambr on 16.28.9.
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {
    Message findBySenderAndInvoiceNumber(Customer customer, String invoiceId);

    @Query(value = "select * from messages where sender_id = ?1 and invoice_number = ?2 limit 1", nativeQuery = true)
    Message fetchBySenderIdAndInvoiceNumber(Integer customerId, String invoiceId);
}
