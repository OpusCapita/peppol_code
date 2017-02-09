package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.FakeSender;
import com.opuscapita.peppol.outbound.controller.sender.Svefaktura1Sender;
import com.opuscapita.peppol.outbound.controller.sender.TestSender;
import com.opuscapita.peppol.outbound.controller.sender.UblSender;
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

    private final UblSender ublSender;
    private final Svefaktura1Sender svefaktura1Sender;
    private final OutboundErrorHandler outboundErrorHandler;
    private final FakeSender fakeSender;
    private final MessageQueue messageQueue;
    private final TestSender testSender;

    @Value("${peppol.outbound.sending.enabled:false}")
    private boolean sendingEnabled;
    @Value("${peppol.outbound.test.recipient:''}")
    private String testRecipient;

    @Autowired
    public OutboundController(@NotNull OutboundErrorHandler outboundErrorHandler, @NotNull MessageQueue messageQueue,
                              @NotNull UblSender ublSender, @Nullable FakeSender fakeSender, @NotNull Svefaktura1Sender svefaktura1Sender,
                              @Nullable TestSender testSender) {
        this.ublSender = ublSender;
        this.outboundErrorHandler = outboundErrorHandler;
        this.fakeSender = fakeSender;
        this.svefaktura1Sender = svefaktura1Sender;
        this.messageQueue = messageQueue;
        this.testSender = testSender;
    }

    public void send(@NotNull ContainerMessage cm) throws Exception {
        if (cm.getBaseDocument() == null) {
            throw new IllegalArgumentException("There is no document in message: " + cm);
        }

        logger.info("Sending message " + cm.getFileName());
        TransmissionResponse transmissionResponse;

        try {
            if (!sendingEnabled) {
                if (StringUtils.isNotBlank(testRecipient)) {
                    if (testSender != null) {
                        transmissionResponse = testSender.send(cm);
                    } else {
                        logger.warn("Selected to send via test sender but it isn't initialized");
                        return;
                    }
                } else {
                    if (fakeSender != null) {
                        transmissionResponse = fakeSender.send(cm);
                    } else {
                        logger.warn("Selected to send via fake sender but it isn't initialized");
                        return;
                    }
                }
            } else {
                // real sending takes place here
                switch (cm.getBaseDocument().getArchetype()) {
                    case INVALID:
                        throw new IllegalArgumentException("Unable to send invalid documents");
                    case SVEFAKTURA1:
                        transmissionResponse = svefaktura1Sender.send(cm);
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
                    logger.info("Message " + cm.getFileName() + " queued for retry");
                    messageQueue.convertAndSend(next, cm);
                } else {
                    outboundErrorHandler.handleError(cm, ioe);
                }
            } else {
                outboundErrorHandler.handleError(cm, ioe);
            }
        } catch (Exception e) {
            logger.warn("Sending of the message " + cm.getFileName() + " failed with error: " + e.getMessage());
            outboundErrorHandler.handleError(cm, e);
        }

    }

}
