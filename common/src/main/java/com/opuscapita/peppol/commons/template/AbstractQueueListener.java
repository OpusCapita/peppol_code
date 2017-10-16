package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

/**
 * Base class for a standard listener that reads container message and processes it.
 *
 * @author Sergejs.Roze
 */
public abstract class AbstractQueueListener {
    protected final static Logger logger = LoggerFactory.getLogger(AbstractQueueListener.class);

    private final ErrorHandler errorHandler;
    private final StatusReporter reporter;
    private final ContainerMessageSerializer serializer;

    protected AbstractQueueListener(@Nullable ErrorHandler errorHandler, @Nullable StatusReporter statusReporter,
                                    @NotNull ContainerMessageSerializer serializer) {
        this.errorHandler = errorHandler;
        this.reporter = statusReporter;
        this.serializer = serializer;
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public synchronized void receiveMessage(@NotNull ContainerMessage cm) {
        try {
            logger.debug("Message received, file id: " + (cm == null ? "UNAVAILABLE" : cm.getFileName()));
            processMessage(cm);
            if (reporter != null) {
                reporter.report(cm);
            }
        } catch (Exception e) {
            handleError(cm.getCustomerId() == null ? "n/a" : cm.getCustomerId(), e, cm);
        }
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull String message) {
        ContainerMessage cm = null;
        boolean success = true;
        try {
            logger.debug("Received string message, assuming JSON");
            cm = serializer.fromJson(message);
        } catch (Exception e) {
            success = false;
            logger.error("Failed to deserialize received message: " + e.getMessage());
            handleError("n/a", e, null);
        }
        if(success && cm != null) {
            receiveMessage(cm);
        }
        try {
            reportEvent(cm);
        } catch (Exception e) {
            handleError(cm.getCustomerId(), e, cm);
        }
    }

    private void reportEvent(ContainerMessage cm) {
        if(cm != null) {
            EventingMessageUtil.reportEvent(cm, cm.getProcessingInfo().getCurrentStatus());
        }
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull byte[] bytes) {
        logger.debug("Message received as bytes array, assuming JSON");
        receiveMessage(new String(bytes));
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

            logger.warn("Message processing failed. File id: " + fileName + ", " + (StringUtils.isBlank(customerId) ? "" : customerId));
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }

        if (reporter != null) {
            if (cm != null) {
                logger.info("Reporting error about " + cm.getFileName() + " to the eventing");
                reporter.reportError(cm, e);
            } else {
                logger.warn("No container message present, cannot report to eventing");
            }
        } else {
            logger.error("Status reporter is null, cannot report errors");
        }

        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

}
