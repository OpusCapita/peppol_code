package com.opuscapita.peppol.inbound;

import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.mq.MqProperties;
import com.opuscapita.peppol.commons.mq.RabbitMq;
import eu.peppol.PeppolMessageMetaData;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.OxalisMessagePersistenceException;
import eu.peppol.persistence.SimpleMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author Sergejs.Roze
 */
public class CustomMessageRepository extends SimpleMessageRepository {
    private final static String INBOUND_OUTPUT_DIR = "inbound.output.dir";
    private final static String INBOUND_MQ_EXCHANGE = "inbound.output.mq.exchange";
    private final static String INBOUND_MQ_QUEUE = "inbound.output.mq.queue";
    private final static String INBOUND_MQ_HOST = "inbound.output.mq.host";
    private final static String INBOUND_MQ_PORT = "inbound.output.mq.port";
    private final static String INBOUND_MQ_USERNAME = "inbound.output.mq.username";
    private final static String INBOUND_MQ_PASSWORD = "inbound.output.mq.password";

    private final static String PROPERTIES_FILE = "/application.properties";

    private static final Logger logger = LoggerFactory.getLogger(CustomMessageRepository.class);
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(CustomMessageRepository.class.getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            logger.error("Failed to read custom repository settings from " + PROPERTIES_FILE, e);
            throw new IllegalStateException("Failed to read custom repository settings from " + PROPERTIES_FILE, e);
        }

        if (properties.getProperty(INBOUND_OUTPUT_DIR) == null) {
            logger.error("Missing configuration property " + INBOUND_OUTPUT_DIR);
            throw new IllegalStateException("Missing configuration property " + INBOUND_OUTPUT_DIR);
        }
        if (properties.getProperty(INBOUND_MQ_QUEUE) == null) {
            logger.error("Missing configuration property " + INBOUND_MQ_QUEUE);
            throw new IllegalStateException("Missing configuration property " + INBOUND_MQ_QUEUE);
        }
        if (properties.getProperty(INBOUND_MQ_HOST) == null) {
            logger.warn("Configuration property " + INBOUND_MQ_HOST + " not found, using 'localhost' instead");
            properties.setProperty(INBOUND_MQ_HOST, "localhost");
        }

        // TODO open ticket on error
    }

    public CustomMessageRepository() {
        super(new File(properties.getProperty(INBOUND_OUTPUT_DIR)));
    }

    @Override
    public void saveInboundMessage(PeppolMessageMetaData peppolMessageMetaData, InputStream inputStream)
            throws OxalisMessagePersistenceException {
        super.saveInboundMessage(peppolMessageMetaData, inputStream);

        ParticipantId recipient = peppolMessageMetaData.getRecipientId();
        ParticipantId sender = peppolMessageMetaData.getSenderId();

        // line below stolen from the original SimpleMessageRepository, if it changes - we're doomed
        String baseName = properties.getProperty(INBOUND_OUTPUT_DIR) + File.separator +
                String.format("%s/%s", normalizeFilename(recipient.stringValue()), normalizeFilename(sender.stringValue()));

        MqProperties mqProperties = new MqProperties(
                properties.getProperty(INBOUND_MQ_HOST),
                properties.getProperty(INBOUND_MQ_PORT),
                properties.getProperty(INBOUND_MQ_USERNAME),
                properties.getProperty(INBOUND_MQ_PASSWORD));
        MessageQueue mq = new RabbitMq();

        try {
            mq.send(mqProperties, properties.getProperty(INBOUND_MQ_EXCHANGE), properties.getProperty(INBOUND_MQ_QUEUE), baseName);
            logger.info("Received message " + baseName + ".xml");
        } catch (Exception e) {
            logger.error("Failed to send message to MQ: " + mqProperties, e);
            // TODO open ticket
        }

    }

}
