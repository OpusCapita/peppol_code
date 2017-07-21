package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
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

    private WebWatchDogReporterReporter webWatchDogReporterReporter;
    private MessageLevelResponseReporter messageLevelResponseReporter;
    private EventPersistenceReporter eventPersistenceReporter;

    @Autowired
    public ReportingManager(@NotNull WebWatchDogReporterReporter webWatchDogReporterReporter,
                            @NotNull MessageLevelResponseReporter messageLevelResponseReporter,
                            @NotNull EventPersistenceReporter eventPersistenceReporter) {
        this.webWatchDogReporterReporter = webWatchDogReporterReporter;
        this.messageLevelResponseReporter = messageLevelResponseReporter;
        this.eventPersistenceReporter = eventPersistenceReporter;
    }

    public void report(ContainerMessage cm, ErrorHandler errorHandler) {
        try {
            eventPersistenceReporter.process(cm);
        } catch (Exception ex){
            logger.error("EventPersistenceReporter failed with exception: " + ex.getMessage());
            errorHandler.reportWithContainerMessage(cm, ex, ex.getMessage());
        }

        try {
            webWatchDogReporterReporter.process(cm);
        } catch (Exception ex1){
            logger.error("WebWatchdogReporter failed wit exception: " + ex1.getMessage());
            errorHandler.reportWithContainerMessage(cm, ex1, ex1.getMessage());
        }

        try {
            messageLevelResponseReporter.process(cm);
        } catch (Exception ex2){
            logger.error("MessageLevelResponseReporter failed with exception: " + ex2.getMessage());
            errorHandler.reportWithContainerMessage(cm, ex2, ex2.getMessage());
        }
    }
}
