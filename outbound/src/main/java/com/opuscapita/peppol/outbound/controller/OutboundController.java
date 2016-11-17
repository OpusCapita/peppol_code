package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import eu.peppol.outbound.transmission.TransmissionResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundController {
    private final static Logger logger = LoggerFactory.getLogger(OutboundController.class);

    @Value("${peppol.outbound.sending.enabled:false}")
    private boolean sendingEnabled;

    private final UblSender ublSender;
    private final OutboundErrorHandler outboundErrorHandler;
    private final TestSender testSender;

    @Autowired
    public OutboundController(@NotNull UblSender ublSender, @NotNull OutboundErrorHandler outboundErrorHandler, @Nullable TestSender testSender) {
        this.ublSender = ublSender;
        this.outboundErrorHandler = outboundErrorHandler;
        this.testSender = testSender;
    }

    public void send(@NotNull ContainerMessage cm) {
        if (cm.getBaseDocument() == null) {
            throw new IllegalArgumentException("There is no document in message: " + cm);
        }

        logger.info("Sending message " + cm.getFileName());
        TransmissionResponse transmissionResponse;

        try {
            if (!sendingEnabled) {
                if (testSender != null) {
                    transmissionResponse = testSender.send(cm);
                } else {
                    logger.warn("Selected to send via test sender but test sender is not initialized");
                    return;
                }
            } else {
                switch (cm.getBaseDocument().getArchetype()) {
                    case INVALID:
                        throw new IllegalArgumentException("Unable to send invalid documents");
                    case SVEFAKTURA1:
                        transmissionResponse = ublSender.send(cm); // the same as UBL since Oxalis 4
                        break;
                    default:
                        transmissionResponse = ublSender.send(cm);
                }
            }
            logger.info("Message " + cm.getFileName() + " sent with transmission ID = " + transmissionResponse.getTransmissionId());
            cm.setTransactionId(transmissionResponse.getTransmissionId().toString());
        } catch (Exception e) {
            outboundErrorHandler.handleError(cm, e);
        }

    }

}
