package com.opuscapita.peppol.inbound;

import eu.peppol.PeppolMessageMetaData;
import eu.peppol.evidence.TransmissionEvidence;
import eu.peppol.persistence.MessageRepository;
import eu.peppol.persistence.OxalisMessagePersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Sergejs.Roze
 */
public class CustomMessageRepository implements MessageRepository {
    private final static String INBOUND_OUTPUT_DIR = "inbound.output.dir";
    private final static String INBOUND_QUEUE = "inbound.output.queue";

    private static final Logger logger = LoggerFactory.getLogger(CustomMessageRepository.class);
    private static Properties properties;

    public CustomMessageRepository() {
        try {
            properties = new Properties();
            properties.load(CustomMessageRepository.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.error("Failed to load properties from /application.properties, using defaults");
        }
    }

    @Override
    public void saveInboundMessage(PeppolMessageMetaData peppolMessageMetaData, InputStream inputStream) throws OxalisMessagePersistenceException {

    }

    @Override
    public void saveTransportReceipt(TransmissionEvidence transmissionEvidence, PeppolMessageMetaData peppolMessageMetaData) {

    }

    @Override
    public void saveNativeTransportReceipt(byte[] bytes) {
        // not implemented
    }
}
