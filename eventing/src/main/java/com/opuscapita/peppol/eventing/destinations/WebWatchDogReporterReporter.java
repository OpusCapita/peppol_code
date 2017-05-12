package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogMessenger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class WebWatchDogReporterReporter {
    private final static Logger logger = LoggerFactory.getLogger(WebWatchDogReporterReporter.class);
    private final WebWatchDogMessenger webWatchDogMessenger;

    @Value("${peppol.eventing.queue.out.name}")
    private String queueOut;

    @Autowired
    public WebWatchDogReporterReporter(@NotNull WebWatchDogMessenger webWatchDogMessenger) {
        this.webWatchDogMessenger = webWatchDogMessenger;
    }

    public void process(@NotNull ContainerMessage cm) {
        try {
            if (cm.getProcessingInfo().getCurrentEndpoint().getType().equals(ProcessType.OUT_OUTBOUND) && WebWatchDogMessenger.isApplicableForFile(cm.getFileName())) {
                if ("delivered".equals(cm.getProcessingInfo().getCurrentStatus())) {
                    webWatchDogMessenger.sendOk(cm.getFileName());
                } else {
                    webWatchDogMessenger.sendFailed(cm.getFileName());
                }
            } else if (cm.getProcessingInfo().getCurrentEndpoint().getType().equals(ProcessType.OUT_VALIDATION) && WebWatchDogMessenger.isApplicableForFile(cm.getFileName())) {
                if (!"validation passed".equals(cm.getProcessingInfo().getCurrentStatus())) {
                    webWatchDogMessenger.sendInvalid(cm.getFileName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to store Web Watch Dog information for " + cm.getFileName());
        }
        logger.info("Stored Web Watch Dog information for " + cm.getFileName());
    }

}
