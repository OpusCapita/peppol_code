package com.opuscapita.peppol.wwd.webwatchdog;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gets data from the container message and sends web watch dog message.
 *
 * @author Daniil Kalnin
 */
@Component
@Lazy
public class WebWatchDogReporter {
    private final static Logger logger = LoggerFactory.getLogger(WebWatchDogReporter.class);
    private final WebWatchDogMessenger webWatchDogMessenger;

    @Autowired
    public WebWatchDogReporter(@NotNull WebWatchDogMessenger webWatchDogMessenger) {
        this.webWatchDogMessenger = webWatchDogMessenger;
        logger.info("Initialized with WebWatchDogMessenger instance: " + webWatchDogMessenger);
    }

    public void process(@NotNull ContainerMessage cm) {
        if (cm.getProcessingInfo() == null) {
            logger.warn("No processing information for " + cm);
        }
        if (!WebWatchDogMessenger.isApplicableForFile(cm.getFileName())) {
            return;
        }
        try {
            logger.info("Current endpoint: " + cm.getProcessingInfo().getCurrentEndpoint());
            if (cm.getProcessingInfo().getCurrentEndpoint().getType().equals(ProcessType.OUT_OUTBOUND)) {
                if ("delivered".equals(cm.getProcessingInfo().getCurrentStatus())) {
                    webWatchDogMessenger.sendOk(cm.getFileName());
                } else {
                    webWatchDogMessenger.sendFailed(cm.getFileName());
                }
            } else if (cm.getProcessingInfo().getCurrentEndpoint().getType().equals(ProcessType.OUT_VALIDATION)) {
                if (!"validation passed".equals(cm.getProcessingInfo().getCurrentStatus())) {
                    webWatchDogMessenger.sendInvalid(cm.getFileName());
                }
            } else {
                logger.info("Not writing status: " + cm.getProcessingInfo().getCurrentEndpoint().getType().name() + " for " + cm.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to store Web Watch Dog information for " + cm.getFileName());
        }
        logger.info("Finished processing Web Watch Dog information for " + cm.getFileName());
    }

}
