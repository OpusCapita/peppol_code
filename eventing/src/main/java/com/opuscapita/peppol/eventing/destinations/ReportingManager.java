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

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Exception> failures = new HashMap<>();
        try {
            logger.info("Received message about " + cm.getFileName() + ", current endpoint: " +
                    cm.getProcessingInfo().getCurrentEndpoint() + ", status: " + cm.getProcessingInfo().getCurrentStatus());
        } catch (Exception ignored) {
        }

        try {
            eventPersistenceReporter.process(cm);
        } catch (Exception ex) {
            logger.error("EventPersistenceReporter failed with exception: " + ex.getMessage());
            failures.put("Exception during reporting to Events persistence", ex);
        }

        try {
            webWatchDogReporter.process(cm);
        } catch (Exception ex1) {
            logger.error("WebWatchdogReporter failed wit exception: " + ex1.getMessage());
            failures.put("Exception during reporting to Web Watch Dog", ex1);
        }

        try {
            messageLevelResponseReporter.process(cm);
        } catch (Exception ex2) {
            ex2.printStackTrace();
            logger.error("MessageLevelResponseReporter failed with exception: " + ex2.getMessage());
            failures.put("Exception during reporting to MLR", ex2);
        }
        if (errorHandler != null) {
            failures.entrySet().forEach(entry -> errorHandler.reportWithContainerMessage(cm, entry.getValue(), entry.getKey()));
        }
    }
}
