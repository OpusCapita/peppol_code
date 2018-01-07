package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Receives message from MQ and sends it to processing with all necessary conversions and error handling.
 *
 * @author Sergejs.Roze
 */
@Component
public class CommonMessageReceiver {
    private final static Logger logger = LoggerFactory.getLogger(CommonMessageReceiver.class);

    private final ContainerMessageSerializer serializer;
    private final ErrorHandler errorHandler;
    private final ContainerMessageProcessor processor;

    @Autowired
    public CommonMessageReceiver(@NotNull ContainerMessageSerializer serializer, @NotNull ErrorHandler errorHandler,
                                 @NotNull ContainerMessageProcessor processor) {
        this.serializer = serializer;
        this.errorHandler = errorHandler;
        this.processor = processor;
    }

    public synchronized void receiveMessage(@NotNull byte[] bytes) {
        logger.debug("Message received as bytes array, assuming JSON");
        receiveMessage(new String(bytes));
    }

    public synchronized void receiveMessage(@NotNull String message) {
        logger.debug("Received string message, assuming JSON");
        receiveMessage(jsonToContainerMessage(message));
    }

    public synchronized void receiveMessage(@Nullable ContainerMessage cm) {
        if (cm == null) {
            logger.warn("Container message is null, exiting");
            reportError(new IllegalArgumentException("Container message is null"), "Container message is null");
        } else {
            logger.debug("Message received, file id: " + cm.getFileName());
            processor.process(cm);
        }
    }

    private ContainerMessage jsonToContainerMessage(@NotNull String json) {
        try {
            return serializer.fromJson(json);
        } catch (Exception e) {
            logger.warn("Failed to deserialize received message: " + e.getMessage());
            reportError(e, json);
        }
        return null; // unreachable but required
    }

    private void reportError(@NotNull Throwable e, @Nullable String message) {
        try {
            errorHandler.reportWithoutContainerMessage("n/a", e, "Failed to deserialize received message",
                    null, "n/a", "Deserialization failed for message: '" + message + "'");
        } catch (Exception weird) {
            logger.error("Deserialization failed for message: '" + message + "', ERROR: " + e.getMessage());
            logger.error("Reporting to SNC failed", weird);
        }
        logger.info("Container message is unavailable, cannot report to eventing");
        throw new AmqpRejectAndDontRequeueException("Deserialization failed", e);
    }

    public void setContainerMessageConsumer(ContainerMessageConsumer controller) {
        this.processor.setContainerMessageConsumer(controller);
    }

}
