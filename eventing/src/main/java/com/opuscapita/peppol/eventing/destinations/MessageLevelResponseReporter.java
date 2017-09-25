package com.opuscapita.peppol.eventing.destinations;

import com.helger.commons.state.ESuccess;
import com.helger.ubl21.UBL21Writer;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.model.Message;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.eventing.destinations.mlr.MessageLevelResponseCreator;
import com.opuscapita.peppol.eventing.destinations.mlr.model.CustomerRepository;
import com.opuscapita.peppol.eventing.destinations.mlr.model.MessageRepository;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    void process(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException, IOException {
        // nothing to do if there is no info about the file
        if (cm.getDocumentInfo() == null || cm.getProcessingInfo() == null) {
            logger.info("No document in received message, ignoring message");
            return;
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
    private void storeResponse(@NotNull ApplicationResponseType art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
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
            logger.warn("Failed to define where to send MLR for " + cm.getFileName() + ", original source = " + originalSource);
        }

        if (backupEnabled) {
            try {
                String backupMlr = storeBackup(art, cm, result);
                if (!created) {
                    logger.info("Backup MLR stored as " + backupMlr);
                }
            } catch (Exception e) {
                logger.warn("Failed to create MLR backup file: " + e.getMessage());
            }
        }
    }

    protected String fetchOriginalSourceFromDb(ContainerMessage cm) {
        Customer customer = customerRepository.findByIdentifier(cm.getCustomerId());
        if (customer == null) {
            logger.warn("Unable to create standard MLR. Could not fetch customer id for file: " + cm.getFileName());
            return null;
        }
        Message message = messageRepository.findBySenderAndInvoiceNumber(customer, cm.getDocumentInfo().getDocumentId());
        if (message == null) {
            logger.warn("Unable to create standard MLR. Could not fetch original event message for file: " + cm.getFileName());
            return null;
        }
        return message.getOriginalSource();
    }

    private boolean isReprocess(ContainerMessage cm) {
        return cm.getProcessingInfo().getSource().getType() == ProcessType.IN_REPROCESS || cm.getProcessingInfo().getSource().getType() == ProcessType.OUT_REPROCESS;
    }

    private void storeResponse(@NotNull ApplicationResponseType art, @NotNull String fileName) throws IOException {
        logger.info("Storing MLR as " + fileName);

        ESuccess result = UBL21Writer.applicationResponse().write(art, new File(fileName));

        if (result.isSuccess()) {
            logger.info("MLR successfully stored as " + fileName);
        } else {
            throw new IOException("Failed to create MLR file named " + fileName);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String storeBackup(@NotNull ApplicationResponseType art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        ESuccess rc = UBL21Writer.applicationResponse().write(art, tmp);
        if (!rc.isSuccess()) {
            throw new IOException("Failed to save MLR backup file");
        }

        String fileName = cm.getFileName() + "-" + result + "-mlr.xml";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tmp.toByteArray());
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document info cannot be null");
        }
        return storage.storeLongTerm(cm.getDocumentInfo().getSenderId(), cm.getDocumentInfo().getRecipientId(), fileName, inputStream);
    }
}
