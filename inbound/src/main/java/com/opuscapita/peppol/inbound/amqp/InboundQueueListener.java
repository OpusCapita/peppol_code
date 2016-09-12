package com.opuscapita.peppol.inbound.amqp;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.inbound.controller.InboundController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
public class InboundQueueListener {
    private final static Logger logger = LoggerFactory.getLogger(InboundQueueListener.class);

    @Value("${amqp.queue.out.name}")
    private String queueName;

    private final InboundController controller;
    private final ErrorHandler errorHandler;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public InboundQueueListener(
            @NotNull ErrorHandler errorHandler, @NotNull InboundController controller, @NotNull RabbitTemplate rabbitTemplate) {
        this.errorHandler = errorHandler;
        this.controller = controller;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        String baseName = new String(data);
        String xml = baseName + File.separator + ".xml";

        try {
            ContainerMessage cm = controller.processFile(baseName);
            rabbitTemplate.convertAndSend(queueName, cm);
            logger.info("Successfully processed incoming message: " + baseName + ".xml");
        } catch (Exception e) {
            logger.error("Failed to process inbound file: " + baseName + ".xml", e);
            handleError(baseName, "n/a", e);
        }
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            errorHandler.reportToServiceNow(message, customerId, e, "Failed to process inbound file");
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", e);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
