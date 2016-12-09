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

/**
 * Base class for a standard listener that reads container message and processes it.
 *
 * @author Sergejs.Roze
 */
public abstract class AbstractQueueListener {
    protected final static Logger logger = LoggerFactory.getLogger(AbstractQueueListener.class);

    private final ErrorHandler errorHandler;
    private final StatusReporter reporter;
    private final Gson gson = new Gson();

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

    public synchronized void receiveMessage(@NotNull byte[] bytes) {
        logger.debug("Message received as bytes array, assuming JSON");
        System.out.println(new String(bytes));

        ContainerMessage cm;
        try {
            cm = gson.fromJson(new String(bytes), ContainerMessage.class);
        } catch (Exception e) {
            handleError("n/a", e, null);
            return;
        }
        receiveMessage(cm);
    }

    protected abstract void processMessage(@NotNull ContainerMessage cm) throws Exception;

    private void handleError(@NotNull String customerId, @NotNull Exception e, @Nullable ContainerMessage cm) {
        try {
            if (errorHandler != null) {
                String message = cm == null ? "no content available" : new String(cm.getBytes());
                errorHandler.reportToServiceNow(message, customerId, e);
            }
            String fileName = (cm == null ? "n/a" : cm.getFileName());

            logger.warn("Message processing failed. File id: " + fileName + ", " + (StringUtils.isBlank(customerId) ? "n/a" : customerId), e);

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
