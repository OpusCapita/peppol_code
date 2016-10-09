package com.opuscapita.peppol.outbound.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundQueueListener {
    private final static Logger logger = LoggerFactory.getLogger(OutboundQueueListener.class);

    private final OutboundController controller;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Autowired
    public OutboundQueueListener(@Nullable ErrorHandler errorHandler, @NotNull OutboundController controller, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.controller = controller;
        this.gson = gson;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        ContainerMessage cm = gson.fromJson(new String(data), ContainerMessage.class);

        try {
            controller.send(cm);
            logger.info("Successfully sent message: " + cm.getFileName());
        } catch (Exception e) {
            logger.error("Failed to send message: " + cm.getFileName(), e);
            handleError("Failed to send message: " + cm.getFileName(), cm.getCustomerId(), e);
        }
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(message, customerId, e, "Failed to send message");
            } else {
                logger.error(message + ", customerId: " + customerId, e);
            }
        } catch (Exception weird) {
            logger.error(message + ", customerId: " + customerId, e);
            logger.error("Reporting to ServiceNow threw exception: ", e);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
