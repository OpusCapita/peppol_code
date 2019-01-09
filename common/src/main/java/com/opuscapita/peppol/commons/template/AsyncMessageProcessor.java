package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Sends message to custom processor and takes responsibility of error handling and events reporting
 * in asynchronous manner. Configuration and error handling must be done separately.
 *
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
@ConditionalOnProperty(name = "peppol.common.async.processing.enabled", havingValue = "true")
public class AsyncMessageProcessor implements ContainerMessageProcessor {
    private final static Logger logger = LoggerFactory.getLogger(AsyncMessageProcessor.class);

    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;

    private ContainerMessageConsumer containerMessageConsumer;

    @Autowired
    public AsyncMessageProcessor(@Nullable StatusReporter statusReporter, @NotNull ErrorHandler errorHandler) {
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
    }

    public void setContainerMessageConsumer(@NotNull ContainerMessageConsumer controller) {
        this.containerMessageConsumer = controller;
    }

    @Async
    public void process(@NotNull ContainerMessage cm) {
        try {
            logger.info("Processing message: " + cm.toLog());
            containerMessageConsumer.consume(cm);
        } catch (Exception e) {
            logger.warn("Message processing failed for " + cm.toLog() + " with error: " + e.getMessage());
            CommonMessageProcessor.reportError(cm, e, errorHandler, statusReporter);
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }

        CommonMessageProcessor.reportStatus(cm, statusReporter);
    }


}
