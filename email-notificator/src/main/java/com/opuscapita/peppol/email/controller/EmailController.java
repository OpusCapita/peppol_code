package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
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
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


/**
 * Prepares files for e-mail sender from received messages.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EmailController extends ValuesChecker {
    public static final String EXT_TO = ".to";
    public static final String EXT_SUBJECT = ".subj";
    public static final String EXT_BODY = ".body";
    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final CustomerRepository customerRepository;
    private final ErrorHandler errorHandler;
    private final BodyFormatter bodyFormatter;
    private final OxalisErrorRecognizer oxalisErrorRecognizer;

    @FileMustExist
    @Value("${peppol.email-notificator.directory}")
    private String directory;
    @Value("${peppol.email-notificator.out.invalid.subject}")
    private String outInvalidEmailSubject;
    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;
    @Value("${peppol.email-notificator.out.lookup-error.subject}")
    private String outLookupErrorEmailSubject;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public EmailController(@NotNull CustomerRepository customerRepository, @Nullable ErrorHandler errorHandler,
                           @NotNull BodyFormatter bodyFormatter, @NotNull OxalisErrorRecognizer oxalisErrorRecognizer) {
        this.customerRepository = customerRepository;
        this.errorHandler = errorHandler;
        this.bodyFormatter = bodyFormatter;
        this.oxalisErrorRecognizer = oxalisErrorRecognizer;
    }

    public void processMessage(@NotNull ContainerMessage cm) throws Exception {
        logger.info("Message received: " + cm.getFileName());

        String customerId = cm.getCustomerId();
        if (StringUtils.isBlank(customerId)) {
            String message = "Cannot determine customer ID from the file: " + cm.getFileName();
            logger.warn(message);
            EventingMessageUtil.reportEvent(cm, "Email notificator failure. "+message);
            if (errorHandler != null) {
                errorHandler.reportWithContainerMessage(cm, null, message);
            }
            return;
        }

        Customer customer = customerRepository.findByIdentifier(customerId);

        if (customer == null) {
            String message = "Customer not found in the database: " + customerId;
            logger.warn(message);
            EventingMessageUtil.reportEvent(cm, "Email notificator failure. "+message);
            if (errorHandler != null) {
                errorHandler.reportWithContainerMessage(cm, null, message);
            }
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
        if (cm.getDocumentInfo() == null) {
            String msg = "Document is null";
            EventingMessageUtil.reportEvent(cm, "Email notificator failure. "+msg);
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        // if (cm.getDocumentInfo() == null || cm.getDocumentInfo().getErrors().size() == 0) {
        if (!cm.hasErrors()) {
            String message = "Document received by email-notificator has no errors: " + cm.getFileName();
            EventingMessageUtil.reportEvent(cm, "Email notificator failure. "+message);
            throw new IllegalArgumentException(message);
        }

        // let's create 3 files per message: list of recipients, subjects in one line, bodies
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName + EXT_TO)))) {
            printWriter.print(emails);
            storeDocument(cm, fileName);
            EventingMessageUtil.reportEvent(cm, "Successfully sent e-mail for file: " + cm.getFileName());
        } catch (Exception e) {
            logger.error("Failed to store e-mail files: ", e);
            EventingMessageUtil.reportEvent(cm, "Failed to store e-mail files " + e.getMessage());
            deleteSilently(fileName + EXT_TO);
            deleteSilently(fileName + EXT_BODY);
            deleteSilently(fileName + EXT_SUBJECT);
        }
    }

    // try to delete files if possible, ignores all exceptions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteSilently(@NotNull String fileName) {
        try {
            new File(fileName).delete();
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void storeDocument(ContainerMessage cm, String fileName) throws IOException {

        storeSubject(getSubjectForContainerMessage(cm), fileName);

        File body = new File(fileName + EXT_BODY);

        if (!body.exists()) {
            Files.write(body.toPath(), ("This is an automatically redirected electronic invoice rejection message:\n\n" +
                            "The following PEPPOL invoice(s) have been rejected by the operator (see Subject).\n\n" +
                            "Please correct invoice(s) and resend.\n\n" +
                            "If you have any questions concerning the rejection, please reply directly to this e-mail.").getBytes(),
                    StandardOpenOption.CREATE);
        }
        Files.write(new File(fileName + EXT_BODY).toPath(), bodyFormatter.format(cm).getBytes(), StandardOpenOption.APPEND);
        logger.info("Message about file " + cm.getFileName() + " stored");
    }

    private String getSubjectForContainerMessage(ContainerMessage cm) {
        String subject = cm.isInbound() ? inInvalidEmailSubject : outInvalidEmailSubject;
        String errorMessage = cm.getProcessingInfo() == null ? null : cm.getProcessingInfo().getProcessingException();
        if (cm.getProcessingInfo() != null && errorMessage != null) {
            if (oxalisErrorRecognizer.recognize(errorMessage).equals(SendingErrors.UNKNOWN_RECIPIENT)) {
                return outLookupErrorEmailSubject;
            }
        }
        return subject;
    }

    @SuppressWarnings("SameParameterValue")
    private void storeSubject(String subject, String fileName) throws IOException {
        if (new File(fileName + EXT_SUBJECT).exists()) {
            subject = "\n" + subject;
        }
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName + EXT_SUBJECT, true)))) {
            printWriter.print(subject);
        }
    }

    // in case we know the customer but she has no e-mail set
    private void processNoEmail(Customer customer, ContainerMessage cm) {
        String message = "E-mail address not set for the customer " + customer.getIdentifier() + " (" + customer.getName() + ")";
        logger.warn(message);
        EventingMessageUtil.reportEvent(cm, "Email notificator failure. "+message);
        if (errorHandler != null) {
            errorHandler.reportWithContainerMessage(cm, null, message);
        }
    }

    private String getFileName(String customerId) {
        return directory + File.separator + customerId;
    }

    // for unit tests
    void setDirectory(@NotNull String directory) {
        this.directory = directory;
    }

    // for unit tests
    void setOutInvalidEmailSubject(@NotNull String outInvalidEmailSubject) {
        this.outInvalidEmailSubject = outInvalidEmailSubject;
    }

    // for unit tests
    void setOutLookupErrorEmailSubject(@NotNull String outLookupErrorEmailSubject) {
        this.outLookupErrorEmailSubject = outLookupErrorEmailSubject;
    }

    // for unit tests
    void setInInvalidEmailSubject(@NotNull String inInvalidEmailSubject) {
        this.inInvalidEmailSubject = inInvalidEmailSubject;
    }

}
