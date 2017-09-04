package com.opuscapita.peppol.eventing.destinations;

import com.helger.commons.state.ESuccess;
import com.helger.ubl21.UBL21Writer;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.eventing.destinations.mlr.MessageLevelResponseCreator;
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

    @Value("${peppol.eventing.mlr.a2a}")
    private String destinationA2A;
    @Value("${peppol.eventing.mlr.xib}")
    private String destinationXiB;
    @Value("${peppol.eventing.mlr.backup.enabled:false}")
    private boolean backupEnabled;

    private final MessageLevelResponseCreator creator;
    private final Storage storage;

    @Autowired
    public MessageLevelResponseReporter(@NotNull MessageLevelResponseCreator creator, @NotNull Storage storage) {
        this.creator = creator;
        this.storage = storage;
    }

    // only messages about errors and successfull delivery must get through
    void process(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException, IOException {
        // nothing to do if there is no info about the file
        if (cm.getDocumentInfo() == null || cm.getProcessingInfo() == null) {
            logger.info("No document in received message, ignoring message");
            return;
        }

        ProcessingInfo pi = cm.getProcessingInfo();
        DocumentInfo di = cm.getDocumentInfo();

        // report errors
        if (di.getArchetype() == Archetype.INVALID && !cm.isInbound()) {
            logger.info("Creating MLR for invalid message: " + cm.getFileName());
            storeResponse(creator.reportError(cm), cm, "re");
            return;
        }

        // processing exception
        if (pi.getProcessingException() != null) {
            logger.info("Creating MLR for message failed due to processing error: " + cm.getFileName());
            storeResponse(creator.reportError(cm), cm, "er");
            return;
        }

        // report retries in outbound
        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_PEPPOL_RETRY) {
            // FIXME MLRs for retries temporary disabled due to Swedish people being unable to provide specification
//            if (di.getErrors().isEmpty()) {
//                logger.info("Creating MLR for message queued for retry");
//                storeResponse(creator.reportRetry(cm), cm, "ab");
//            } else {
//                logger.info("Creating MLR for message with errors: " + cm.getFileName());
//                storeResponse(creator.reportError(cm), cm, "re");
//            }
            return;
        }

        // report successfull end of the flow
        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_OUTBOUND) {
            if (di.getErrors().isEmpty()) {
                if (StringUtils.isNotBlank(pi.getTransactionId())) {
                    logger.info("Creating MLR for successfully sent message: " + cm.getFileName());
                    storeResponse(creator.reportSuccess(cm), cm, "ap");
                } else {
                    logger.warn("Skipping MLR creation for " + cm.getFileName() + ", seems to be sending retry");
                }
            } else {
                logger.info("Creating MLR for message with errors: " + cm.getFileName());
                storeResponse(creator.reportError(cm), cm, "re");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void storeResponse(@NotNull ApplicationResponseType art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
        if (StringUtils.containsIgnoreCase(cm.getProcessingInfo().getSource().getName(), "a2a")) {
            storeResponse(art, destinationA2A + File.separator + FilenameUtils.getBaseName(cm.getFileName()) +
                    "-" + result + "-mlr.xml");
        }
        if (StringUtils.containsIgnoreCase(cm.getProcessingInfo().getSource().getName(), "xib")) {
            storeResponse(art, destinationXiB + File.separator + FilenameUtils.getBaseName(cm.getFileName()) +
                    "-" + result + "-mlr.xml");
        }
        if (backupEnabled) {
            try {
                storeBackup(art, cm, result);
            } catch (Exception e) {
                logger.warn("Failed to create MLR backup file: " + e.getMessage());
            }
        }
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

    private void storeBackup(@NotNull ApplicationResponseType art, @NotNull ContainerMessage cm, @NotNull String result) throws IOException {
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
        storage.storeLongTerm(cm.getDocumentInfo().getSenderId(), cm.getDocumentInfo().getRecipientId(), fileName, inputStream);
    }
}
