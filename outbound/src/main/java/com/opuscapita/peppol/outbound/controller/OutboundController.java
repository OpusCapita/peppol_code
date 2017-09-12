package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
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

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundController {
    private final static Logger logger = LoggerFactory.getLogger(OutboundController.class);

    private final UblSender ublSender;
    private final Svefaktura1Sender svefaktura1Sender;
    private final FakeSender fakeSender;
    private final MessageQueue messageQueue;
    private final TestSender testSender;
    private final OxalisErrorRecognizer oxalisErrorRecognizer;

    @Value("${peppol.outbound.sending.enabled:false}")
    private boolean sendingEnabled;
    @Value("${peppol.outbound.test.recipient:''}")
    private String testRecipient;
    @Value("${peppol.component.name}")
    private String componentName;

    @Autowired
    public OutboundController(@NotNull MessageQueue messageQueue,
                              @NotNull UblSender ublSender, @Nullable FakeSender fakeSender, @NotNull Svefaktura1Sender svefaktura1Sender,
                              @Nullable TestSender testSender, @NotNull OxalisErrorRecognizer oxalisErrorRecognizer) {
        this.ublSender = ublSender;
        this.fakeSender = fakeSender;
        this.svefaktura1Sender = svefaktura1Sender;
        this.messageQueue = messageQueue;
        this.testSender = testSender;
        this.oxalisErrorRecognizer = oxalisErrorRecognizer;
    }

    @SuppressWarnings("ConstantConditions")
    public void send(@NotNull ContainerMessage cm) throws Exception {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("There is no document in message: " + cm);
        }
        Endpoint endpoint = new Endpoint(componentName, ProcessType.OUT_OUTBOUND);

        logger.info("Sending message " + cm.getFileName());
        TransmissionResponse transmissionResponse;

        try {
            if (!sendingEnabled) {
                if (fakeSender != null) {
                    transmissionResponse = fakeSender.send(cm);
                } else {
                    logger.warn("Selected to send via fake sender but it isn't initialized");
                    return;
                }
            } else {
                if (StringUtils.isNotBlank(testRecipient)) {
                    // real send via test sender
                    if (testSender != null) {
                        transmissionResponse = testSender.send(cm);
                    } else {
                        logger.warn("Selected to send via test sender but it isn't initialized");
                        return;
                    }
                } else {
                    // production sending takes place here
                    switch (cm.getDocumentInfo().getArchetype()) {
                        case INVALID:
                            throw new IllegalArgumentException("Unable to send invalid documents");
                        case SVEFAKTURA1:
                            transmissionResponse = svefaktura1Sender.send(cm);
                            break;
                        default:
                            transmissionResponse = ublSender.send(cm);
                    }
                }
            }
            logger.info("Message " + cm.getFileName() + " sent with transmission ID = " + transmissionResponse.getTransmissionId());
            cm.getProcessingInfo().setTransactionId(transmissionResponse.getTransmissionId().toString());
            cm.getProcessingInfo().setCommonName(transmissionResponse.getCommonName() != null ? transmissionResponse.getCommonName().toString() : "N/A");
            cm.getProcessingInfo().setSendingProtocol(transmissionResponse.getProtocol() != null ? transmissionResponse.getProtocol().toString() : "N/A");
        } catch (Exception e) {
            logger.warn("Sending of the message " + cm.getFileName() + " failed with I/O error: " + e.getMessage());
            whatAboutRetry(cm, messageQueue, e, endpoint);
        }
    }

    // will try to re-send the message to the delayed queue only for I/O exceptions
    @SuppressWarnings("ConstantConditions")
    private void whatAboutRetry(@NotNull ContainerMessage cm, @NotNull MessageQueue messageQueue,
                                @NotNull Exception e, @NotNull Endpoint endpoint) throws Exception {
        cm.setStatus(endpoint, "message delivery failure");

        SendingErrors errorType = oxalisErrorRecognizer.recognize(e);
        if (!errorType.isTemporary()) {
            logger.info("Exception of type " + errorType + " registered as non-retriable, rejecting message " + cm.getFileName());
            cm.getProcessingInfo().setProcessingException(e.getMessage());
            throw e;
        }

        if (cm.getProcessingInfo() != null && cm.getProcessingInfo().getRoute() != null) {
            String next = cm.popRoute();
            if (StringUtils.isNotBlank(next)) {
                cm.setStatus(new Endpoint(next, ProcessType.OUT_PEPPOL_RETRY), "retry: " + next);
                messageQueue.convertAndSend(next, cm);
                logger.info("Message " + cm.getFileName() + " queued for retry to the queue " + next);
                return;
            }
        }

        logger.info("No (more) retries possible for " + cm.getFileName() + ", reporting IO error");
        cm.getProcessingInfo().setProcessingException(e.getMessage());
        throw e;
    }

    // for unit tests
    @SuppressWarnings("SameParameterValue")
    void setComponentName(@NotNull String componentName) {
        this.componentName = componentName;
    }

}
