package com.opuscapita.peppol.inbound;

import eu.peppol.persistence.ExtendedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Sergejs.Roze
 */
public class InboundProperties {
    private final static Logger logger = LoggerFactory.getLogger(InboundProperties.class);

    public final static String INBOUND_OUTPUT_DIR = "inbound.output.dir";
    public final static String INBOUND_MQ_EXCHANGE = "inbound.output.mq.exchange";
    public final static String INBOUND_MQ_QUEUE = "inbound.output.mq.queue";
    public final static String INBOUND_MQ_HOST = "preprocessing.output.mq.host";
    public final static String INBOUND_MQ_PORT = "preprocessing.output.mq.port";
    public final static String INBOUND_MQ_USERNAME = "preprocessing.output.mq.username";
    public final static String INBOUND_MQ_PASSWORD = "preprocessing.output.mq.password";

    private final static String PROPERTIES_FILE = "/application.properties";

    private final static Properties it = new Properties();

    static {
        try {
            it.load(ExtendedMessageRepository.class.getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            logger.error("Failed to read custom repository settings from " + PROPERTIES_FILE, e);
            throw new IllegalStateException("Failed to read custom repository settings from " + PROPERTIES_FILE, e);
        }

        if (it.getProperty(INBOUND_OUTPUT_DIR) == null) {
            logger.error("Missing configuration property " + INBOUND_OUTPUT_DIR);
            throw new IllegalStateException("Missing configuration property " + INBOUND_OUTPUT_DIR);
        }
        if (it.getProperty(INBOUND_MQ_QUEUE) == null) {
            logger.error("Missing configuration property " + INBOUND_MQ_QUEUE);
            throw new IllegalStateException("Missing configuration property " + INBOUND_MQ_QUEUE);
        }
        if (it.getProperty(INBOUND_MQ_HOST) == null) {
            logger.warn("Configuration property " + INBOUND_MQ_HOST + " not found, using 'localhost' instead");
            it.setProperty(INBOUND_MQ_HOST, "localhost");
        }

        // TODO open ticket on error
    }

    public String getProperty(String key) {
        return it.getProperty(key);
    }

}
