package com.opuscapita.peppol.eventing.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.eventing.controller.EventingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class EventingListener {
    private static final Logger logger = LoggerFactory.getLogger(EventingListener.class);

    private final EventingController controller;
    private final ErrorHandler errorHandler;
    private final Gson gson;
    private final RabbitTemplate rabbitTemplate;

    @Value("{peppol.eventing.queue.out.name}")
    private String outQueue;

    @Autowired
    public EventingListener(@NotNull EventingController controller, @Nullable ErrorHandler errorHandler, @NotNull Gson gson,
                            @NotNull RabbitTemplate rabbitTemplate) {
        this.controller = controller;
        this.errorHandler = errorHandler;
        this.gson = gson;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull byte[] data) {
        String message = new String(data);
        logger.debug("Message received");

        ContainerMessage cm = null;
        try {
            cm = gson.fromJson(message, ContainerMessage.class);
            logger.info("Processing message about file: " + cm.getFileName());
            PeppolEvent result = controller.process(cm);
            logger.debug("Sending result");
            rabbitTemplate.convertAndSend(outQueue, result);
        } catch (Exception e) {
            handleError(cm, e);
        }
    }

    private void handleError(@Nullable ContainerMessage cm, @NotNull Exception e) {
        try {
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(e.getMessage(), cm == null ? BaseDocument.UNKNOWN_SENDER : cm.getCustomerId(), e);
            }
            logger.error("Failed to process event: ", e);
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

}
