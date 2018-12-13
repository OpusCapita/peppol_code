package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.FakeSender;
import com.opuscapita.peppol.outbound.controller.sender.PeppolSender;
import com.opuscapita.peppol.outbound.controller.sender.RealSender;
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

    private final RealSender realSender;
    private final FakeSender fakeSender;
    private final RealSender testSender;

    private final ErrorHandler errorHandler;
    private final MessageQueue messageQueue;
    private final StatusReporter eventReporter;
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
    public OutboundController(@NotNull RealSender realSender, @Nullable FakeSender fakeSender, @Nullable RealSender testSender,
                              @NotNull MessageQueue messageQueue, @NotNull ErrorHandler errorHandler,
                              @Nullable StatusReporter eventReporter, @NotNull OxalisErrorRecognizer oxalisErrorRecognizer) {
        this.realSender = realSender;
        this.fakeSender = fakeSender;
        this.testSender = testSender;
        this.errorHandler = errorHandler;
        this.messageQueue = messageQueue;
        this.eventReporter = eventReporter;
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
            handleException(cm, e);
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

        logger.info("Selected to send via REAL sender");
        return realSender;
    }

    private void handleException(@NotNull ContainerMessage cm, @NotNull Exception e) throws Exception {
        Endpoint endpoint = new Endpoint(componentName, ProcessType.OUT_OUTBOUND);
        cm.setStatus(endpoint, "message delivery failure");

        SendingErrors errorType = oxalisErrorRecognizer.recognize(e);

        if (errorType.isRetryable()) {
            String retry = cm.popRoute();
            if (StringUtils.isBlank(retry)) {
                logger.info("No (more) retries possible for " + cm.getFileName() + ", reporting IO error");
                cm.getProcessingInfo().setProcessingException(e.getMessage());
                throw e;
            }

            cm.setStatus(new Endpoint(retry, ProcessType.OUT_PEPPOL_RETRY), "RETRY: " + retry);
            messageQueue.convertAndSend(retry, cm);
            logger.info("Message " + cm.getFileName() + " queued for retry to the queue " + retry);
            return;
        }

        logger.info("Exception of type " + errorType + " registered as non-retriable, rejecting message " + cm.getFileName());
        cm.getProcessingInfo().setProcessingException(e.getMessage());
        createEventReport(cm, e);

        if (errorType.isTicketable()) {
            createSNCTicket(cm, e);
        } else {
            sendEmailNotification(cm);
        }
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

    private void sendEmailNotification(@NotNull ContainerMessage cm) {
        try {
            messageQueue.convertAndSend(emailNotificatorQueue, cm);
        } catch (Exception weird) {
            logger.error("Reporting to email-notificator threw exception: ", weird);
        }
    }

    private void createSNCTicket(@NotNull ContainerMessage cm, @NotNull Exception e) {
        try {
            errorHandler.reportWithContainerMessage(cm, e, e.getMessage() == null ? "null" : e.getMessage());
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
    }

    private void createEventReport(@NotNull ContainerMessage cm, @NotNull Exception e) {
        try {
            if (eventReporter != null) {
                eventReporter.reportError(cm, e);
            }
        } catch (Exception weird) {
            logger.error("Failed to report event using event status reporter", weird);
        }
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
