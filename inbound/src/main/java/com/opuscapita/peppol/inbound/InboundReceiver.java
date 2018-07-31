package com.opuscapita.peppol.inbound;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.storage.StorageImpl;
import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.persist.PersisterHandler;
import no.difi.oxalis.api.util.Type;
import no.difi.vefa.peppol.common.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.opuscapita.peppol.inbound.InboundProperties.*;

@Singleton
@Type("opuscapita")
public class InboundReceiver implements PersisterHandler {
    private final static Logger logger = LoggerFactory.getLogger(InboundReceiver.class);

    private final InboundProperties properties = new InboundProperties();
    private final InboundErrorHandler errorHandler;
    private final Gson gson;
    private final Storage storage;

    @SuppressWarnings("ConstantConditions")
    public InboundReceiver() {
        errorHandler = new InboundErrorHandler(properties);
        gson = new GsonBuilder().disableHtmlEscaping().create();
        storage = new StorageImpl().setShortTermDirectory(properties.getProperty(INBOUND_OUTPUT_DIR));
        logger.info("OpusCapita inbound receiver initialized");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Path persist(TransmissionIdentifier transmissionIdentifier, Header header, InputStream inputStream) throws IOException {

        // store message first, everything else can wait
        String dataFile;
        try {
            dataFile = storage.storeTemporary(inputStream,
                    transmissionIdentifier + ".xml",
                    properties.getProperty(INBOUND_COPY_DIR));
            logger.info("Message stored as: " + dataFile);
        } catch (Exception e) {
            logger.error("Failed to store incoming message", e);
            errorHandler.report("Failed to store incoming message", e.getMessage(), header.getReceiver().toString());
            throw new IOException("Failed to store incoming message", e);
        }

        // the message is stored at this moment, must not send any exception to sending party anymore
        ContainerMessage cm = null;

        // send file to MQ, no exception to sending AP here anymore, file already available
        try {
            logger.info("Message metadata: " + header);

            Endpoint endpoint = new Endpoint(properties.getProperty(COMPONENT_NAME), ProcessType.IN_INBOUND);
            cm = new ContainerMessage(header.toString(), dataFile, endpoint);
            cm.getProcessingInfo().setTransactionId(transmissionIdentifier.toString());
            cm.setStatus(cm.getProcessingInfo().getSource(), "received");
            EventingMessageUtil.reportEvent(cm, "received file");
            new InboundMessageSender(properties).send(cm);
        } catch (Exception e) {
            logger.error("Failed to report file " + dataFile + " to MQ: ", e);
            String transmissionId = transmissionIdentifier.toString();
            errorHandler.report("Failed to report received file with transmission id " + transmissionId + " to message queue",
                    e.getMessage() + header.toString(),
                    header.getReceiver().toString());
            logger.error("Metadata dump: " + header.toString());
            if (cm != null) {
                logger.error("ContainerMessage dump: " + gson.toJson(cm));
            }
        }

        return Paths.get(dataFile);
    }

    @Override
    public void persist(InboundMetadata inboundMetadata, Path payloadPath) {
        logger.info("Transmission receipt for " + payloadPath.toString() + ":\n" + gson.toJson(inboundMetadata));
    }
}
