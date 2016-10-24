package com.opuscapita.peppol.transport.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.transport.contoller.TransportController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class TransportQueueListener {
    private static Logger logger = LoggerFactory.getLogger(TransportQueueListener.class);

    private final TransportController controller;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Autowired
    public TransportQueueListener(@NotNull Gson gson, @Nullable ErrorHandler errorHandler, @NotNull TransportController controller) {
        this.gson = gson;
        this.errorHandler = errorHandler;
        this.controller = controller;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data);

        try {
            logger.debug("Message received");
            ContainerMessage cm = gson.fromJson(message, ContainerMessage.class);
            controller.storeMessage(cm);
            logger.debug("Message stored");
        } catch (Exception e) {
            logger.error("Failed to store message: " + e.getMessage(), e);
            handleError("Failed to store message", "", e);
        }

    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(message, customerId, e, "Failed to persist event");
            }
            logger.error(message + ", customer: " + customerId, e);
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

}
