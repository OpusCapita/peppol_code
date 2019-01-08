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
 * @deprecated Replaced with CommonMessageReceiver and two optional processors
 *
 * @author Sergejs.Roze
 */
@Deprecated
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
        boolean eventReported = false;
        try {
            logger.debug("Message received, file id: " + (cm == null ? "UNAVAILABLE" : cm.getFileName()));
            processMessage(cm);
            reportEvent(cm);
            if (reporter != null) {
                reporter.report(cm);
            }
            eventReported = true;
        } catch (Exception e) {
            handleError(cm, e);
        }
        if(!eventReported) {
            try {
                reportEvent(cm);
            } catch (Exception e) {
                handleError(cm, e);
            }
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
            handleError(null, e);
        }
        if(success && cm != null) {
            receiveMessage(cm);
        }

    }

    private void reportEvent(ContainerMessage cm) {
        if (cm != null && cm.getProcessingInfo() != null) {
            logger.info("Reporting event: " + cm.getProcessingInfo().getEventingMessage() + " for message: " + cm.toLog());
            EventingMessageUtil.reportEvent(cm, cm.getProcessingInfo().getCurrentStatus());
        }
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull byte[] bytes) {
        logger.debug("Message received as bytes array, assuming JSON");
        receiveMessage(new String(bytes));
    }

    protected abstract void processMessage(@NotNull ContainerMessage cm) throws Exception;

    private void handleError(@Nullable ContainerMessage cm, @NotNull Exception e) {
        try {
            if (errorHandler != null) {
                if (cm == null) {
                    errorHandler.reportWithoutContainerMessage("n/a", e, e.getMessage(), null, null);
                } else {
                    errorHandler.reportWithContainerMessage(cm, e, e.getMessage());
                }
            }

            logger.warn("Message processing failed. Message: " + (cm == null ? "null" : cm.toLog()));
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }

        if (reporter != null) {
            if (cm != null) {
                logger.info("Reporting error about " + cm.toLog() + " to the eventing");
                reporter.reportError(cm, e);
            } else {
                logger.warn("No container message present, cannot report to eventing");
            }
        } else {
            logger.info("Status reporter is not set, skipping report to eventing");
        }

        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

}
