package com.opuscapita.peppol.mlr.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.model.Message;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.mlr.util.model.CustomerRepository;
import com.opuscapita.peppol.mlr.util.model.MessageRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * Checks whether we should report the message. If yes - creates the file in given directory.
 *
 * @author Sergejs.Roze
 */
@Component
public class MessageLevelResponseReporter {
    private final static Logger logger = LoggerFactory.getLogger(MessageLevelResponseReporter.class);
    private final MessageLevelResponseCreator creator;
    private final Storage storage;
    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;
    @Value("${peppol.eventing.mlr.a2a}")
    private String destinationA2A;
    @Value("${peppol.eventing.mlr.xib}")
    private String destinationXiB;
    @Value("${peppol.eventing.mlr.backup.enabled:false}")
    private boolean backupEnabled;

    @Autowired
    public MessageLevelResponseReporter(@NotNull MessageLevelResponseCreator creator, @NotNull Storage storage, CustomerRepository customerRepository, MessageRepository messageRepository) {
        this.creator = creator;
        this.storage = storage;
        this.customerRepository = customerRepository;
        this.messageRepository = messageRepository;
    }

    // only messages about errors and successfull delivery must get through
    public void process(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException, IOException {
        // nothing to do if there is no info about the file
        if (cm.getDocumentInfo() == null || cm.getProcessingInfo() == null) {
            logger.info("No document in received message, ignoring message");
            return;
        }

        if(cm.getFileName().contains("eventing_invalid-snc-test.xml")){
            throw new IllegalStateException("Integration test only");
        }

        // ignoring inbound
        if (cm.isInbound()) {
            return;
        }

        ProcessingInfo pi = cm.getProcessingInfo();
        DocumentInfo di = cm.getDocumentInfo();

        // report invalid files
        if (di.getArchetype() == Archetype.INVALID) {
            logger.info("Creating MLR (re) for: invalid message: " + cm.getFileName());
            storeResponse(creator.reportError(cm), cm, "re");
            return;
        }

        // processing exception
        if (pi.getProcessingException() != null) {
            logger.info("Creating MLR (er) for: message in error because of processing exception: " + cm.getFileName());
            storeResponse(creator.reportError(cm), cm, "er");
            return;
        }

        // errors reported in file
        if (!di.getErrors().isEmpty()) {
            logger.info("Creating MLR (re) for: message with errors: " + cm.getFileName());
            storeResponse(creator.reportError(cm), cm, "re");
            return;
        }

        // report retries in outbound
        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_PEPPOL_RETRY) {
            logger.info("Creating MLR (ab) for: message queued for retry: " + cm.getFileName());
            storeResponse(creator.reportRetry(cm), cm, "ab");
            return;
        }

        // report successfull end of the flow
        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_OUTBOUND) {
            if (StringUtils.isNotBlank(pi.getTransactionId())) {
                logger.info("Creating MLR (ap) for: successfully sent message: " + cm.getFileName());
                storeResponse(creator.reportSuccess(cm), cm, "ap");
            } else {
                logger.error("Message " + cm.getFileName() + " reported as successfully sent out but there is no transmission ID");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void storeResponse(@NotNull String art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
        boolean created = false;
        String originalSource = cm.getProcessingInfo().getOriginalSource();
        if (isReprocess(cm)) {
            originalSource = fetchOriginalSourceFromDb(cm);
        }

        if (StringUtils.containsIgnoreCase(originalSource, "a2a")) {
            storeResponse(art, destinationA2A + File.separator + FilenameUtils.getBaseName(cm.getFileName()) +
                    "-" + result + "-mlr.xml");
            created = true;
        }
        if (StringUtils.containsIgnoreCase(originalSource, "xib")) {
            storeResponse(art, destinationXiB + File.separator + FilenameUtils.getBaseName(cm.getFileName()) +
                    "-" + result + "-mlr.xml");
            created = true;
        }
        if (!created) {
            logger.error("Failed to define where to send MLR for " + cm.getFileName() + ", original source = " + originalSource);
        }

        if (backupEnabled) {
            try {
                String backupMlr = storeBackup(art, cm, result);
                if (!created) {
                    logger.info("Backup MLR stored as " + backupMlr);
                }
            } catch (Exception e) {
                logger.error("Failed to create MLR backup file: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String fetchOriginalSourceFromDb(ContainerMessage cm) {
        Customer customer = customerRepository.findByIdentifier(cm.getCustomerId());
        if (customer == null) {
            logger.warn("Unable to create standard MLR. Could not fetch customer for file: " + cm.getFileName()
                    + " and customer id: " + cm.getDocumentInfo().getSenderId());
            return null;
        }
        Message message = messageRepository.findBySenderAndInvoiceNumber(customer, cm.getDocumentInfo().getDocumentId());
        if (message == null) {
            logger.warn("Unable to create standard MLR. Could not fetch original event message for file: " + cm.getFileName());
            return null;
        }
        return message.getOriginalSource();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isReprocess(ContainerMessage cm) {
        return cm.getProcessingInfo().getSource().getType() == ProcessType.IN_REPROCESS || cm.getProcessingInfo().getSource().getType() == ProcessType.OUT_REPROCESS;
    }

    private void storeResponse(@NotNull String art, @NotNull String fileName) throws IOException {
        logger.info("Storing MLR as " + fileName);

        Files.write(Paths.get(fileName), art.getBytes(StandardCharsets.UTF_8));
        logger.info("MLR successfully stored as " + fileName);
    }

    @SuppressWarnings("ConstantConditions")
    private String storeBackup(@NotNull String art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
        String fileName = FilenameUtils.getBaseName(cm.getFileName()) + "-" + result + "-mlr.xml";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(art.getBytes(StandardCharsets.UTF_8));
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document info cannot be null");
        }
        return storage.storeLongTerm(cm.getDocumentInfo().getSenderId(), cm.getDocumentInfo().getRecipientId(), fileName, inputStream);
    }

}
