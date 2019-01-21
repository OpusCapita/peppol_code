package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Sends message to custom processor and takes responsibility of error handling and events reporting.
 *
 * @author Sergejs.Roze
 */
@Component
@ConditionalOnProperty(name = "peppol.common.async.processing.enabled", havingValue = "false", matchIfMissing = true)
public class CommonMessageProcessor implements ContainerMessageProcessor {
    private final static Logger logger = LoggerFactory.getLogger(CommonMessageProcessor.class);

    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;

    private ContainerMessageConsumer containerMessageConsumer;

    @Autowired
    public CommonMessageProcessor(@Nullable StatusReporter statusReporter, @NotNull ErrorHandler errorHandler) {
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
    }

    public void setContainerMessageConsumer(@NotNull ContainerMessageConsumer controller) {
        this.containerMessageConsumer = controller;
    }

    public void process(@NotNull ContainerMessage cm) {
        try {
            logger.info("Processing message: " + cm.toLog());
            containerMessageConsumer.consume(cm);
        } catch (Exception e) {
            logger.warn("Message processing failed for " + cm.toLog() + " with error: " + e.getMessage());
            reportError(cm, e, errorHandler, statusReporter);
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }

        reportStatus(cm, statusReporter);
    }

    static void reportStatus(@NotNull ContainerMessage cm, @Nullable StatusReporter statusReporter) {
        if (statusReporter != null) {
            statusReporter.report(cm);
        }

        try {
            if (cm.getProcessingInfo() != null) {
                EventingMessageUtil.reportEvent(cm, cm.getProcessingInfo().getCurrentStatus());
            } else {
                throw new IllegalStateException("Processing info is missing from container message");
            }
        } catch (Exception e) {
            logger.error("Failed to report message status: " + e.getMessage(), e);
        }
    }

    static void reportError(@NotNull ContainerMessage cm, @NotNull Throwable e,
                            @NotNull ErrorHandler errorHandler, @Nullable StatusReporter statusReporter) {
        try {
            String shortDescription = ErrorHandler.getShortDescription(cm, e);
            errorHandler.reportWithContainerMessage(cm, e, e.getMessage());
            logger.warn("Message processing failed. " + shortDescription);
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }

        try {
            if (statusReporter != null) {
                statusReporter.reportError(cm, e);
            }
        } catch (Exception weird) {
            logger.error("Failed to report status using status reporter", weird);
        }
    }

}
