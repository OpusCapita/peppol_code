package com.opuscapita.peppol.inbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.mq.ConnectionString;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.mq.MqProperties;
import com.opuscapita.peppol.commons.mq.RabbitMqStandalone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.opuscapita.peppol.inbound.InboundProperties.*;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class InboundMessageSender {
    private final static Logger logger = LoggerFactory.getLogger(InboundMessageSender.class);

    private final InboundProperties properties;

    public InboundMessageSender(InboundProperties properties) {
        this.properties = properties;
    }

    public void send(ContainerMessage cm) throws IOException, TimeoutException {
        MessageQueue mq = new RabbitMqStandalone(prepareMqProperties());
        logger.debug("Sending message to " + properties.getProperty(INBOUND_MQ_QUEUE) + " about file: " + cm.getFileName());
        mq.send(properties.getProperty(INBOUND_MQ_QUEUE) + ConnectionString.QUEUE_SEPARATOR +
                ConnectionString.EXCHANGE + ConnectionString.VALUE_SEPARATOR + properties.getProperty(INBOUND_MQ_EXCHANGE), cm);
    }

    private MqProperties prepareMqProperties() {
        return new MqProperties(
                properties.getProperty(INBOUND_MQ_HOST),
                properties.getProperty(INBOUND_MQ_PORT),
                properties.getProperty(INBOUND_MQ_USERNAME),
                properties.getProperty(INBOUND_MQ_PASSWORD));
    }
}
