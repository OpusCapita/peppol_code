package com.opuscapita.peppol.internal_routing.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.internal_routing.controller.RoutingController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by KALNIDA1 on 2016.07.12..
 */
@Component
public class EventQueueListener {
    Logger logger = LoggerFactory.getLogger(EventQueueListener.class);

    private final RoutingController controller;
    private final ErrorHandler errorHandler;
    private final Gson gson;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventQueueListener(
            @NotNull Gson gson, @NotNull ErrorHandler errorHandler, @NotNull RoutingController controller, @NotNull RabbitTemplate rabbitTemplate) {
        this.gson = gson;
        this.errorHandler = errorHandler;
        this.controller = controller;
        this.rabbitTemplate = rabbitTemplate;
    }

    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data);
        logger.debug("Received message: " + message);

        try {
            ContainerMessage cm = gson.fromJson(message, ContainerMessage.class);
            cm = controller.loadRoute(cm);
            logger.debug("Route set to " + cm.getRoute());
            rabbitTemplate.convertAndSend(cm.getRoute().pop("Route defined: " + cm.getRoute()), cm);
        } catch (Exception e) {
            logger.error("Failed to read message: " + e.getMessage(), e);
            handleError("Internal routing failed to process received AMQP message", "", e);
        }

    }

    private void handleError(String message, String customerId, Exception e) {
        errorHandler.reportToServiceNow(message, customerId, e, "Failed processing message in internal routing");
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
