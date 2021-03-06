package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.eventing.revised.MessageAttemptEventReporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gamanse1 on 2017.07.18..
 */
@Component
@Lazy
public class ReportingManager {
    private final static Logger logger = LoggerFactory.getLogger(ReportingManager.class);

    private EventPersistenceReporter eventPersistenceReporter;
    private MessageAttemptEventReporter messageAttemptEventReporter;
    private MessageQueue rabbitTemplate;
    @Value("${peppol.eventing.queue.out.mlr.name}")
    private String mlrQueue;

    @Autowired
    public ReportingManager(@NotNull EventPersistenceReporter eventPersistenceReporter,
                            @NotNull MessageAttemptEventReporter messageAttemptEventReporter,
                            @NotNull MessageQueue rabbitTemplate) {
        this.eventPersistenceReporter = eventPersistenceReporter;
        this.messageAttemptEventReporter = messageAttemptEventReporter;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SuppressWarnings("ConstantConditions")
    public void process(ContainerMessage cm, @Nullable ErrorHandler errorHandler) {
        Map<String, Exception> processingExceptions = new HashMap<>();
        try {
            logger.info("Received message: " + cm.toLog());
        } catch (Exception ignored) {
        }

        try {
            eventPersistenceReporter.process(cm);
        } catch (Exception ex) {
            logger.error("EventPersistenceReporter failed with exception: " + ex.getMessage());
            processingExceptions.put("Exception during reporting to Events persistence", ex);
        }

        if (!cm.isInbound()) {
            try {
                rabbitTemplate.convertAndSend(mlrQueue, cm);
            } catch (Exception ex2) {
                ex2.printStackTrace();
                logger.error("MessageLevelResponseReporter failed with exception: " + ex2.getMessage());
                processingExceptions.put("Exception during reporting to MLR", ex2);
            }
        }

        try {
            messageAttemptEventReporter.process(cm);
        } catch (Exception ex3) {
            ex3.printStackTrace();
            logger.error("MessageAttemptEventReporter failed with exception: " + ex3.getMessage());
            processingExceptions.put("Exception during reporting with new eventing", ex3);
        }


        if (errorHandler != null) {
            processingExceptions.forEach((key, value) -> errorHandler.reportWithContainerMessage(cm, value, key));
        } else {
            logger.warn("ErrorHandler is null, this should be avoided in production!");
        }
    }
}
