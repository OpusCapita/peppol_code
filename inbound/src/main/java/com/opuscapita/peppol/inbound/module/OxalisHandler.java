package com.opuscapita.peppol.inbound.module;

import com.google.gson.Gson;
import com.opuscapita.peppol.inbound.InboundApp;
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

/**
 * Receives message from Oxalis and gives it to us. Exception in persist method will be propagated to the other party
 * so one has to be careful.
 */
@Singleton
@Type("opuscapita")
public class OxalisHandler implements PersisterHandler {
    private final static Logger logger = LoggerFactory.getLogger(OxalisHandler.class);

    private final MessageHandler messageHandler;

    @SuppressWarnings("ConstantConditions")
    public OxalisHandler() {
        // this is done to separate Spring dependency injection from Guice one (we're in Guice now, while messageHandler is in Spring)
        messageHandler = InboundApp.getMessageHandler();
        logger.info("OpusCapita inbound receiver initialized");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Path persist(TransmissionIdentifier transmissionIdentifier, Header header, InputStream inputStream) throws IOException {
        String transmissionId = header.getIdentifier().getIdentifier();
        String dataFile = messageHandler.preProcess(transmissionId, header, inputStream);
        return Paths.get(dataFile);
    }

    @Override
    public void persist(InboundMetadata inboundMetadata, Path payloadPath) {
        messageHandler.process(inboundMetadata, payloadPath);

        logger.info("Transmission receipt for " + payloadPath.toString() + ":\n" + new Gson().toJson(inboundMetadata));
    }
}
