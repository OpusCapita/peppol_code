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
import org.springframework.stereotype.Component;

/**
 * Sends message to custom processor and takes responsibility of error handling and events reporting.
 *
 * @author Sergejs.Roze
 */
@Component
public class CommonMessageProcessor {
    private final static Logger logger = LoggerFactory.getLogger(CommonMessageProcessor.class);

    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;

    private ContainerMessageConsumer controller;

    @Autowired
    public CommonMessageProcessor(@Nullable StatusReporter statusReporter, @NotNull ErrorHandler errorHandler) {
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
    }

    public void setController(@NotNull ContainerMessageConsumer controller) {
        this.controller = controller;
    }

    public void process(@NotNull ContainerMessage cm) {
        try {
            logger.info("Processing message " + cm.getFileName());
            controller.consume(cm);
        } catch (Exception e) {
            logger.warn("Message processing failed: " + e.getMessage());
            reportError(cm, e);
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }

        reportStatus(cm);
    }

    private void reportStatus(@NotNull ContainerMessage cm) {
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
            logger.error("Failed to report message status: " + e.getMessage());
        }
    }

    private void reportError(@NotNull ContainerMessage cm, @NotNull Throwable e) {
        try {
            errorHandler.reportWithContainerMessage(cm, e, e.getMessage() == null ? "null" : e.getMessage());
            String customerId = cm.getCustomerId() == null ? "n/a" : cm.getCustomerId();
            logger.warn("Message processing failed. File id: " + cm.getFileName() + ", customer ID = " + customerId +
                    ", reason: " + e.getMessage());
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }

        if (statusReporter != null) {
            statusReporter.reportError(cm, e);
        }
    }

}
