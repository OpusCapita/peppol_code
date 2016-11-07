package com.opuscapita.peppol.commons.template;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.context.annotation.Bean;

/**
 * Base class for a standard listener that reads container message and processes it.
 *
 * @author Sergejs.Roze
 */
public abstract class AbstractQueueListener {
    protected final static Logger logger = LoggerFactory.getLogger(AbstractQueueListener.class);

    private final ErrorHandler errorHandler;
    private final StatusReporter reporter;
    private final Gson gson;

    protected AbstractQueueListener(@Nullable ErrorHandler errorHandler, @Nullable StatusReporter statusReporter, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.reporter = statusReporter;
        this.gson = gson;
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public synchronized void receiveMessage(byte[] data) {
        ContainerMessage cm = null;

        try {
            String message = new String(data);
            logger.debug("Message received");
            cm = gson.fromJson(message, ContainerMessage.class);
        } catch (Exception e) {
            handleError("Failed to parse incoming message", "", e, null);
        }

        try {
            processMessage(cm);
            logger.debug("Message processed");

            reporter.report(cm);
        } catch (Exception e) {
            handleError("Failed to process message", cm.getCustomerId() == null ? "" : cm.getCustomerId(), e, cm);
        }
    }

    protected abstract void processMessage(@NotNull ContainerMessage cm) throws Exception;

    private void handleError(@NotNull String message, @NotNull String customerId, @NotNull Exception e, @Nullable ContainerMessage cm) {
        try {
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(message, customerId, e);
            }
            logger.error(message + ", customer: " + customerId, e);

            if (cm != null && reporter != null) {
                reporter.reportError(cm, e, message);
            }
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

    @Bean
    Gson gson() {
        return new Gson();
    }

}
