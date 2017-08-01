package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by gamanse1 on 2017.07.18..
 */
@Component
@Lazy
public class ReportingManager {
    private final static Logger logger = LoggerFactory.getLogger(MessageLevelResponseReporter.class);

    private WebWatchDogReporter webWatchDogReporter;
    private MessageLevelResponseReporter messageLevelResponseReporter;
    private EventPersistenceReporter eventPersistenceReporter;

    @Autowired
    public ReportingManager(@NotNull WebWatchDogReporter webWatchDogReporter,
                            @NotNull MessageLevelResponseReporter messageLevelResponseReporter,
                            @NotNull EventPersistenceReporter eventPersistenceReporter) {
        this.webWatchDogReporter = webWatchDogReporter;
        this.messageLevelResponseReporter = messageLevelResponseReporter;
        this.eventPersistenceReporter = eventPersistenceReporter;
    }

    @SuppressWarnings("ConstantConditions")
    public void process(ContainerMessage cm, @Nullable ErrorHandler errorHandler) {
        try {
            logger.info("Received message about " + cm.getFileName() + ", current endpoint: " +
                    cm.getProcessingInfo().getCurrentEndpoint() + ", status: " + cm.getProcessingInfo().getCurrentStatus());
        } catch (Exception ignored) {}

        try {
            eventPersistenceReporter.process(cm);
        } catch (Exception ex) {
            logger.error("EventPersistenceReporter failed with exception: " + ex.getMessage());
            if (errorHandler != null) {
                errorHandler.reportWithContainerMessage(cm, ex, ex.getMessage());
            }
        }

        try {
            webWatchDogReporter.process(cm);
        } catch (Exception ex1) {
            logger.error("WebWatchdogReporter failed wit exception: " + ex1.getMessage());
            if (errorHandler != null) {
                errorHandler.reportWithContainerMessage(cm, ex1, ex1.getMessage());
            }
        }

        try {
            messageLevelResponseReporter.process(cm);
        } catch (Exception ex2) {
            logger.error("MessageLevelResponseReporter failed with exception: " + ex2.getMessage());
            if (errorHandler != null) {
                errorHandler.reportWithContainerMessage(cm, ex2, ex2.getMessage());
            }
        }
    }
}
