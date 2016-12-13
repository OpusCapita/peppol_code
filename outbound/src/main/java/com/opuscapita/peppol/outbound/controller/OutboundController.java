package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.Route;
import eu.peppol.outbound.transmission.TransmissionResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
    private final RabbitTemplate rabbitTemplate;
    @Value("${peppol.outbound.sending.enabled:false}")
    private boolean sendingEnabled;

    @Autowired
    public OutboundController(@NotNull OutboundErrorHandler outboundErrorHandler, @NotNull RabbitTemplate rabbitTemplate,
                              @NotNull UblSender ublSender, @Nullable TestSender testSender, @NotNull Svefaktura1Sender svefaktura1Sender) {
        this.ublSender = ublSender;
        this.outboundErrorHandler = outboundErrorHandler;
        this.testSender = testSender;
        this.svefaktura1Sender = svefaktura1Sender;
        this.rabbitTemplate = rabbitTemplate;
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
                Map<String, String> next = cm.getRoute().popFull();
                if (StringUtils.isNotBlank(next.get(Route.NEXT))) {
                    returnToMessageQueue(cm, next);
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

    private void returnToMessageQueue(ContainerMessage cm, Map<String, String> destination) {
        logger.info("Resending message " + cm.getFileName() + " for retry");

        String queue = destination.get(Route.NEXT);
        String delay = destination.get(DELAY);

        if (StringUtils.isNotBlank(delay)) {
            rabbitTemplate.convertAndSend(queue, cm, message -> {
                message.getMessageProperties().setDelay(Integer.parseInt(delay));
                return message;
            });
        } else {
            rabbitTemplate.convertAndSend(queue, cm);
        }
    }
}
