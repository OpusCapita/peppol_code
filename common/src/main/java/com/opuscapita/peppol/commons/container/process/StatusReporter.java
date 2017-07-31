package com.opuscapita.peppol.commons.container.process;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reports processing status to eventing application.
 * In case of failure sends the message to ServiceNow.
 *
 * @author Sergejs.Roze
 */
@Component
public class StatusReporter {
    private static final Logger logger = LoggerFactory.getLogger(StatusReporter.class);

    private final MessageQueue rabbitTemplate;
    private final ErrorHandler errorHandler;

    @Value("${peppol.eventing.queue.in.name}")
    private String reportDestination;

    @Autowired
    public StatusReporter(@NotNull MessageQueue rabbitTemplate, @NotNull ErrorHandler errorHandler) {
        this.rabbitTemplate = rabbitTemplate;
        this.errorHandler = errorHandler;
    }

    public void report(@NotNull ContainerMessage cm) {
        try {
            rabbitTemplate.convertAndSend(reportDestination, cm);
        } catch (Exception e) {
            failedToProcess(cm, e, "Failed to report service status");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void reportError(@NotNull ContainerMessage cm, @Nullable Exception e) {
        if (cm.getProcessingInfo() == null) {
            cm.setProcessingInfo(new ProcessingInfo(
                    new Endpoint("status_reporter", ProcessType.UNKNOWN), "Process info missing in Container Message"));
        }
        cm.getProcessingInfo().setProcessingException(e);
        try {
            rabbitTemplate.convertAndSend(reportDestination, cm);
            logger.info("Error message about " + cm.getFileName() + " send to " + reportDestination +
                    ", endpoint: " + cm.getProcessingInfo().getCurrentEndpoint() + ", status: " + cm.getProcessingInfo().getCurrentStatus());
        } catch (Exception exception) {
            failedToProcess(cm, e, "Failed to report service error");
        }
    }

    private void failedToProcess(ContainerMessage cm, Exception e, String shortDescription) {
        logger.warn("Reporting an issue to ServiceNow: " + e.getMessage());
        try {
            if (cm == null) {
                errorHandler.reportWithoutContainerMessage("n/a", e, shortDescription, null, null);
            } else {
                errorHandler.reportWithContainerMessage(cm, e, e.getMessage());
            }
        } catch (Exception exception) {
            logger.error("Failed to report issue to ServiceNow: ", exception);
        }
    }

}
