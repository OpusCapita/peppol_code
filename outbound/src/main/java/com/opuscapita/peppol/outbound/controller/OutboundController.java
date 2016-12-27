package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.mq.RabbitMq;
import eu.peppol.outbound.transmission.TransmissionResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundController {
    private final static Logger logger = LoggerFactory.getLogger(OutboundController.class);
    private final static String DELAY = "x-delay";
    private final UblSender ublSender;
    private final Svefaktura1Sender svefaktura1Sender;
    private final OutboundErrorHandler outboundErrorHandler;
    private final TestSender testSender;
    private final RabbitMq rabbitMq;
    @Value("${peppol.outbound.sending.enabled:false}")
    private boolean sendingEnabled;

    @Autowired
    public OutboundController(@NotNull OutboundErrorHandler outboundErrorHandler, @NotNull RabbitMq rabbitMq,
                              @NotNull UblSender ublSender, @Nullable TestSender testSender, @NotNull Svefaktura1Sender svefaktura1Sender) {
        this.ublSender = ublSender;
        this.outboundErrorHandler = outboundErrorHandler;
        this.testSender = testSender;
        this.svefaktura1Sender = svefaktura1Sender;
        this.rabbitMq = rabbitMq;
    }

    public void send(@NotNull ContainerMessage cm) throws Exception {
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
                        transmissionResponse = svefaktura1Sender.send(cm); // the same as UBL since Oxalis 4
                        break;
                    default:
                        transmissionResponse = ublSender.send(cm);
                }
            }
            logger.info("Message " + cm.getFileName() + " sent with transmission ID = " + transmissionResponse.getTransmissionId());
            cm.setTransactionId(transmissionResponse.getTransmissionId().toString());
        } catch (IOException ioe) {
            logger.warn("Sending of the message " + cm.getFileName() + " failed with error: " + ioe.getMessage());

            // try to retry if it is defined in route
            if (cm.getRoute() != null) {
                String next = cm.getRoute().pop();
                if (StringUtils.isNotBlank(next)) {
                    rabbitMq.send(next, cm);
                } else {
                    outboundErrorHandler.handleError(cm, ioe);
                }
            } else {
                outboundErrorHandler.handleError(cm, ioe);
            }
        } catch (Exception e) {
            outboundErrorHandler.handleError(cm, e);
        }

    }

}
