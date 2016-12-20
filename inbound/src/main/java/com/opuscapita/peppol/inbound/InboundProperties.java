package com.opuscapita.peppol.inbound;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author Sergejs.Roze
 */
public class InboundProperties {
    private final static Logger logger = LoggerFactory.getLogger(InboundProperties.class);

    public final static String INBOUND_OUTPUT_DIR = "peppol.storage.short";

    public final static String COMPONENT_NAME = "peppol.component.name";
    final static String INBOUND_MQ_EXCHANGE = "peppol.inbound.queue.exchange";
    final static String INBOUND_MQ_QUEUE = "peppol.inbound.queue.name";
    final static String INBOUND_MQ_HOST = "peppol.inbound.queue.host";
    final static String INBOUND_MQ_PORT = "peppol.inbound.queue.port";
    final static String INBOUND_MQ_USERNAME = "peppol.inbound.queue.username";
    final static String INBOUND_MQ_PASSWORD = "peppol.inbound.queue.password";

    private final static String PROPERTIES_FILE = "application.properties";

    private final static Properties it = new Properties();

    static {
        try {
            File source = new File(PROPERTIES_FILE);
            it.load(new FileInputStream(source));
        } catch (Exception e) {
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
        if (StringUtils.isBlank(it.getProperty(COMPONENT_NAME))) {
            logger.warn("Configuration property " + COMPONENT_NAME + " is missing, using 'inbound' instead");
            it.setProperty(COMPONENT_NAME, "inbound");
        }
    }

    @Nullable
    public String getProperty(String key) {
        return it.getProperty(key);
    }

    @NotNull Properties getProperties() {
        return it;
    }

}
