package com.opuscapita.peppol.transport.controller;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.transport.JmsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class TransportController {
    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);

    private final MessageProducer messageProducer;
    private final Gson gson;
    private final Session session;

    @Autowired
    public TransportController(MessageProducer messageProducer, Session session, Gson gson) {
        this.messageProducer = messageProducer;
        this.gson = gson;
        this.session = session;
    }

    public void send(ContainerMessage cm) throws IOException, JMSException {
        String payload = new String(Base64.getEncoder().encode(Files.readAllBytes(Paths.get(cm.getFileName()))));
        JmsPayload jmsPayload = new JmsPayload(payload, new HashMap<String, Object>() {{
            put("created", cm.getProcessingInfo().getEventingMessage().getCreated());
        }});
        messageProducer.send(session.createTextMessage(gson.toJson(payload)), DeliveryMode.PERSISTENT, javax.jms.Message.DEFAULT_DELIVERY_MODE, javax.jms.Message.DEFAULT_TIME_TO_LIVE);
    }
}
