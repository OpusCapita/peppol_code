package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.outbound.controller.sf1.Svefaktura1Sender;
import eu.peppol.outbound.transmission.TransmissionResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundController {
    private final static Logger logger = LoggerFactory.getLogger(OutboundController.class);

    private final UblSender ublSender;
    private final Svefaktura1Sender svefaktura1Sender;
    private final OutboundErrorHandler outboundErrorHandler;

    @Autowired
    public OutboundController(@NotNull UblSender ublSender, @NotNull Svefaktura1Sender svefaktura1Sender,
            @NotNull OutboundErrorHandler outboundErrorHandler) {
        this.ublSender = ublSender;
        this.svefaktura1Sender = svefaktura1Sender;
        this.outboundErrorHandler = outboundErrorHandler;
    }

    public void send(@NotNull ContainerMessage cm) {
        if (cm.getBaseDocument() == null) {
            throw new IllegalArgumentException("There is no document in message: " + cm);
        }

        logger.info("Sending message " + cm.getFileName());
        TransmissionResponse transmissionId;

        try {
            switch (cm.getBaseDocument().getArchetype()) {
                case INVALID:
                    throw new IllegalArgumentException("Unable to send invalid documents");
                case SVEFAKTURA1:
                    transmissionId = ublSender.send(cm);
                    break;
                default:
                    transmissionId = ublSender.send(cm);
            }
            logger.info("Message " + cm.getFileName() + " sent with transmission ID = " + transmissionId);
        } catch (Exception e) {
            outboundErrorHandler.handleError(cm, e);
        }

    }

}
