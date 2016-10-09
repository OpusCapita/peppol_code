package com.opuscapita.peppol.inbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.mq.MqProperties;
import com.opuscapita.peppol.commons.mq.RabbitMq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.opuscapita.peppol.inbound.InboundProperties.*;

/**
 * @author Sergejs.Roze
 */
public class InboundMessageSender {
    private final static Logger logger = LoggerFactory.getLogger(InboundMessageSender.class);

    private final InboundProperties properties;

    public InboundMessageSender(InboundProperties properties) {
        this.properties = properties;
    }

    public void send(ContainerMessage cm) throws IOException, TimeoutException {
        MessageQueue mq = new RabbitMq();
        mq.send(prepareMqProperties(), properties.getProperty(INBOUND_MQ_EXCHANGE), properties.getProperty(INBOUND_MQ_QUEUE), cm);
    }

    private MqProperties prepareMqProperties() {
        return new MqProperties(
                properties.getProperty(INBOUND_MQ_HOST),
                properties.getProperty(INBOUND_MQ_PORT),
                properties.getProperty(INBOUND_MQ_USERNAME),
                properties.getProperty(INBOUND_MQ_PASSWORD));
    }
}
