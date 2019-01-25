package com.opuscapita.peppol.inbound.module;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.storage.Storage;
import no.difi.oxalis.api.inbound.InboundMetadata;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Handles incoming message.
 *
 * @author Sergejs.Roze
 */
@Component
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${peppol.inbound.copy.directory:}")
    private String copyDirectory;

    @Value("${peppol.component.name}")
    private String componentName;

    private final Storage storage;
    private final ErrorHandler errorHandler;
    private final MessageSender messageSender;

    @Autowired
    public MessageHandler(@NotNull Storage storage, @NotNull ErrorHandler errorHandler, @NotNull MessageSender messageSender) {
        this.storage = storage;
        this.errorHandler = errorHandler;
        this.messageSender = messageSender;
    }

    @NotNull
    String preProcess(String transmissionId, InputStream inputStream) throws IOException {
        return storeTemporary(transmissionId, inputStream);
    }

    // this is the only method that allowed to throw an exception which will be propagated to the sending party
    private String storeTemporary(String transmissionId, InputStream inputStream) throws IOException {
        try {
            String result = storage.storeTemporary(inputStream, transmissionId + ".xml", copyDirectory);
            logger.info("Received message stored as " + result);
            return result;
        } catch (Exception e) {
            fail("Failed to store message " + transmissionId + ".xml", transmissionId, e);
            throw new IOException("Failed to store message " + transmissionId + ".xml: " + e.getMessage(), e);
        }
    }

    private void fail(String message, String transmissionId, Exception e) {
        logger.error(message, e);
        try {
            errorHandler.reportWithoutContainerMessage(null, e, message, transmissionId, transmissionId + ".xml");
        } catch (Exception ex) {
            logger.error("Failed to report error '" + message + "'", ex);
        }
    }

    void process(InboundMetadata inboundMetadata, Path payloadPath) {
        ContainerMessage cm = createContainerMessage(payloadPath.toString());
        cm.getProcessingInfo().setPeppolMessageMetadata(PeppolMessageMetadata.create(inboundMetadata));
        messageSender.send(cm);
    }

    private ContainerMessage createContainerMessage(String dataFile) {
        Endpoint endpoint = new Endpoint(componentName, ProcessType.IN_INBOUND);
        ContainerMessage cm = new ContainerMessage(dataFile, endpoint);
        cm.setStatus(cm.getProcessingInfo().getSource(), "received");
        EventingMessageUtil.reportEvent(cm, "received file");
        return cm;
    }
}
