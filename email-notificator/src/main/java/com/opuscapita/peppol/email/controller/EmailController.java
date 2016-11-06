package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.email.model.CustomerRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Prepares files for e-mail sender from received messages.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EmailController {
    public static final String EXT_TO = ".to";
    public static final String EXT_SUBJECT = ".subj";
    public static final String EXT_BODY = ".body";
    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final CustomerRepository customerRepository;
    private final ErrorHandler errorHandler;

    @Value("${peppol.email-notificator.directory}")
    private String directory;

    @Autowired
    public EmailController(@NotNull CustomerRepository customerRepository, @Nullable ErrorHandler errorHandler) {
        this.customerRepository = customerRepository;
        this.errorHandler = errorHandler;
    }

    public void processMessage(@NotNull ContainerMessage cm) throws Exception {
        if (!(cm.getBaseDocument() instanceof InvalidDocument)) {
            logger.error("Expected invalid document while received " + cm.getBaseDocument().getClass().getName());
            throw new IllegalStateException("Message is not in invalid state");
        }

        String customerId = cm.getCustomerId();
        if (StringUtils.isBlank(customerId)) {
            processNoCustomer(cm);
        }

        Customer customer = customerRepository.findByIdentifier(customerId);

        if (customer == null) {
            processNoCustomer(cm);
            return;
        }

        String emails;
        if (cm.isInbound()) {
            emails = customer.getInboundEmails();
        } else {
            emails = customer.getOutboundEmails();
        }
        if (StringUtils.isBlank(emails)) {
            processNoEmail(customer, cm);
            return;
        }

        storeMessage(emails, cm);
    }

    private void storeMessage(String emails, ContainerMessage cm) throws IOException {
        String fileName = getFileName(cm.getCustomerId());
        if (cm.getBaseDocument() == null || !(cm.getBaseDocument() instanceof InvalidDocument)) {
            String msg = "Document is not an instance of InvalidDocument but is " + cm.getBaseDocument().getClass().getName() + " instead";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        InvalidDocument doc = (InvalidDocument) cm.getBaseDocument();

        // let's create 3 files per message: list of recipients, subjects in one line, bodies
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName + EXT_TO)))) {
            printWriter.print(emails);
        }

        String subject = StringUtils.isBlank(doc.getReason()) ? "Error in Peppol AP" : doc.getReason();
        if (new File(fileName + EXT_SUBJECT).exists()) {
            subject = "\n" + subject;
        }
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName + EXT_SUBJECT, true)))) {
            printWriter.print(subject);
        }

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName + EXT_BODY, true)))) {
            printWriter.print(cm.getBaseDocument().toString());
        }
    }

    // in case we know the customer but she has no e-mail set
    private void processNoEmail(Customer customer, ContainerMessage cm) {
        String message = "E-mail address not set for the customer " + customer.getIdentifier() + " (" + customer.getName() + ")";
        logger.warn(message);
        if (errorHandler != null) {
            errorHandler.reportToServiceNow(message, customer.getIdentifier(), null, "Customer e-mail address missing");
        }
    }

    // in case we cannot identify customer
    private void processNoCustomer(ContainerMessage cm) {
        String message = "Cannot determine customer ID from the file: " + cm.getFileName();
        logger.warn(message);
        if (errorHandler != null) {
            errorHandler.reportToServiceNow(message, "n/a", null, "Failed to find customer ID");
        }
    }

    private String getFileName(String customerId) {
        return directory + File.separator + customerId;
    }

}
