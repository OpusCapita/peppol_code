package com.opuscapita.peppol.commons.template;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

/**
 * Base class for a standard listener that reads container message and processes it.
 *
 * @author Sergejs.Roze
 */
public abstract class AbstractQueueListener {
    protected final static Logger logger = LoggerFactory.getLogger(AbstractQueueListener.class);

    private final ErrorHandler errorHandler;
    private final StatusReporter reporter;
    private final Gson gson = ContainerMessage.prepareGson();

    protected AbstractQueueListener(@Nullable ErrorHandler errorHandler, @Nullable StatusReporter statusReporter) {
        this.errorHandler = errorHandler;
        this.reporter = statusReporter;
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public synchronized void receiveMessage(@NotNull ContainerMessage cm) {
        logger.debug("Message received, file id: " + cm == null ? "UNAVAILABLE" : cm.getFileName());

        try {
            processMessage(cm);
            if (reporter != null) {
                reporter.report(cm);
            }
        } catch (Exception e) {
            handleError(cm.getCustomerId() == null ? "n/a" : cm.getCustomerId(), e, cm);
        }
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull byte[] bytes) {
        logger.debug("Message received as bytes array, assuming JSON");

        ContainerMessage cm;
        try {
            cm = gson.fromJson(new String(bytes), ContainerMessage.class);
            receiveMessage(cm);
        } catch (Exception e) {
            try {
                logger.warn("Failed to deserialize with Gson: " + e.getMessage() + ". Trying to deserialize manually");
                SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
                MessageProperties messageProperties = new MessageProperties();
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT);
                cm = (ContainerMessage) simpleMessageConverter.fromMessage(new Message(bytes, messageProperties));
                receiveMessage(cm);
            } catch (Exception e1) {
                logger.error("Failed to deserialize the message from AMQP: " + e1.getMessage());
                handleError("n/a", e1, null);
            }
        }
    }

    protected abstract void processMessage(@NotNull ContainerMessage cm) throws Exception;

    private void handleError(@NotNull String customerId, @NotNull Exception e, @Nullable ContainerMessage cm) {
        try {
            if (errorHandler != null) {
                if (cm == null) {
                    errorHandler.reportWithoutContainerMessage(customerId, e, e.getMessage(), null, null);
                } else {
                    errorHandler.reportWithContainerMessage(cm, e, e.getMessage());
                }
            }
            String fileName = (cm == null ? "n/a" : cm.getFileName());

            logger.warn("Message processing failed. File id: " + fileName + ", " +
                    (StringUtils.isBlank(customerId) ? "" : customerId), e);
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }

        if (reporter != null) {
            if (cm != null) {
                reporter.reportError(cm, e);
            } else {
                logger.warn("No container message present, cannot report to eventing");
            }
        }

        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

}
