package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.*;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
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
    @Value("${peppol.outbound.test.recipient:}")
    private String testRecipient;
    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String emailNotificatorQueue;

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
        logger.info("Sending message " + cm.getFileName());

        try {
            PeppolSender sender = getPeppolSender(cm);
            if (sender == null) {
                logger.warn("PeppolSender is not initialized");
                return;
            }

            TransmissionResponse transmissionResponse = sender.send(cm);
            logger.info("Message " + cm.getFileName() + " sent with transmission ID = " + transmissionResponse.getTransmissionIdentifier());

            cm.getProcessingInfo().setTransactionId(transmissionResponse.getTransmissionIdentifier().toString());
            cm.getProcessingInfo().setCommonName((transmissionResponse.getHeader() != null && transmissionResponse.getHeader().getReceiver() != null) ? extractCommonName(cm) : "N/A");
            cm.getProcessingInfo().setSendingProtocol(transmissionResponse.getProtocol() != null ? transmissionResponse.getProtocol().toString() : "N/A");
        } catch (Exception e) {
            logger.warn("Sending of the message " + cm.getFileName() + " failed with I/O error: " + e.getMessage());
            whatAboutRetry(cm, messageQueue, e, new Endpoint(componentName, ProcessType.OUT_OUTBOUND));
        }

        if (StringUtils.isNotBlank(cm.getProcessingInfo().getTransactionId())) {
            logger.debug("Message " + cm.getFileName() + "delivered with transaction id = " + cm.getProcessingInfo().getTransactionId());
            cm.setStatus(new Endpoint(componentName, ProcessType.OUT_OUTBOUND), "delivered");
            EventingMessageUtil.reportEvent(cm, "Delivered to Peppol network");
        }
    }

    private PeppolSender getPeppolSender(ContainerMessage cm) {
        if (!sendingEnabled) {
            logger.info("Selected to send via FAKE sender");
            return fakeSender;
        }
        if (StringUtils.isNotBlank(testRecipient)) {
            logger.info("Selected to send via TEST sender");
            return testSender;
        }
        if (Archetype.INVALID.equals(cm.getDocumentInfo().getArchetype())) {
            throw new IllegalArgumentException("Unable to send invalid documents");
        }
        if (Archetype.SVEFAKTURA1.equals(cm.getDocumentInfo().getArchetype())) {
            logger.info("Selected to send via SVEFAKTURA sender");
            return svefaktura1Sender;
        }

        logger.info("Selected to send via UBL sender");
        return ublSender;
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

            // some issues should be solved by e-mail sending - to be removed later
            if (errorType == SendingErrors.DATA_ERROR) {
                logger.info("Sending an e-mail to customer about invalid data");
                messageQueue.convertAndSend(emailNotificatorQueue, cm);
                return;
            }
            if (errorType == SendingErrors.UNKNOWN_RECIPIENT || errorType == SendingErrors.UNSUPPORTED_DATA_FORMAT) {
                logger.info("Sending an e-mail to customer about unknown recipient or unsupported data format");
                messageQueue.convertAndSend(emailNotificatorQueue, cm);
                return;
            }

            throw e;
        }

        if (cm.getProcessingInfo().getRoute() != null) {
            String next = cm.popRoute();
            if (StringUtils.isNotBlank(next)) {
                cm.setStatus(new Endpoint(next, ProcessType.OUT_PEPPOL_RETRY), "RETRY: " + next);
                messageQueue.convertAndSend(next, cm);
                logger.info("Message " + cm.getFileName() + " queued for retry to the queue " + next);
                return;
            }
        }

        logger.info("No (more) retries possible for " + cm.getFileName() + ", reporting IO error");
        cm.getProcessingInfo().setProcessingException(e.getMessage());
        throw e;
    }

    private String extractCommonName(ContainerMessage cm) {
        String result = "";
        if (StringUtils.isNotBlank(cm.getDocumentInfo().getRecipientId())) {
            result += "CN=" + cm.getDocumentInfo().getRecipientId();
        }
        if (StringUtils.isNotBlank(cm.getDocumentInfo().getRecipientName())) {
            result += ",O=" + cm.getDocumentInfo().getRecipientName();
        }
        if (StringUtils.isNotBlank(cm.getDocumentInfo().getRecipientCountryCode())) {
            result += ",C=" + cm.getDocumentInfo().getRecipientCountryCode();
        }
        return result;
    }

    // for unit tests
    @SuppressWarnings("SameParameterValue")
    void setComponentName(@NotNull String componentName) {
        this.componentName = componentName;
    }

    @SuppressWarnings("SameParameterValue")
    void setEmailNotificatorQueue(@NotNull String emailNotificatorQueue) {
        this.emailNotificatorQueue = emailNotificatorQueue;
    }

}
