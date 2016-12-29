package com.opuscapita.peppol.commons.container.status;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
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
            handleError(cm, e);
        }
    }

    public void reportError(@NotNull ContainerMessage cm, @Nullable Exception e) {
        ProcessingStatus status = cm.getProcessingStatus();
        if (!(cm.getBaseDocument() instanceof InvalidDocument)) {
            cm.setBaseDocument(new InvalidDocument(cm.getBaseDocument(), status.getResult(), e));
        }

        try {
            rabbitTemplate.convertAndSend(reportDestination, cm);
        } catch (Exception exception) {
            handleError(cm, e);
        }
    }

    private void handleError(ContainerMessage cm, Exception e) {
        logger.warn("Reporting an issue to ServiceNow: " + e.getMessage());
        try {
            if (cm == null) {
                errorHandler.reportToServiceNow("{}", "n/a", e);
                return;
            }
            errorHandler.reportToServiceNow(new String(cm.convertToJsonByteArray()), cm.getCustomerId(), e);
        } catch (Exception exception) {
            logger.error("Failed to report issue to ServiceNow: ", exception);
        }
    }

}
