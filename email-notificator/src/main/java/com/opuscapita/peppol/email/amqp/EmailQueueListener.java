package com.opuscapita.peppol.email.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.email.controller.EmailController;
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
public class EmailQueueListener {
    private final static Logger logger = LoggerFactory.getLogger(EmailQueueListener.class);

    private final EmailController controller;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Autowired
    public EmailQueueListener(@Nullable ErrorHandler errorHandler, @NotNull EmailController controller, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.controller = controller;
        this.gson = gson;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data);

        try {
            ContainerMessage cm = gson.fromJson(message, ContainerMessage.class);
            controller.processMessage(cm);
            logger.info("E-mail message stored for processing");
        } catch (Exception e) {
            logger.error("Failed to process e-mail message", e);
            handleError("Failed to process e-mail message", "n/a", e);
        }
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            logger.error("Failed to process e-mail message: " + message + ", customerId = " + customerId, e);
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(message, customerId, e, "Failed to process e-mail message");
            }
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", e);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
