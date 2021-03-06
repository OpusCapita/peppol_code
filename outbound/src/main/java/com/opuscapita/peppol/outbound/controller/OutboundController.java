package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
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
import com.opuscapita.peppol.outbound.controller.sender.TestSender;
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
    private final TestSender testSender;

    private final MessageQueue messageQueue;
    private final ErrorHandler errorHandler;
    private final StatusReporter statusReporter;
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
    public OutboundController(@NotNull RealSender realSender, @Nullable FakeSender fakeSender, @Nullable TestSender testSender,
                              @NotNull MessageQueue messageQueue, @NotNull OxalisErrorRecognizer oxalisErrorRecognizer,
                              @Nullable StatusReporter statusReporter, @NotNull ErrorHandler errorHandler) {
        this.realSender = realSender;
        this.fakeSender = fakeSender;
        this.testSender = testSender;
        this.messageQueue = messageQueue;
        this.errorHandler = errorHandler;
        this.statusReporter = statusReporter;
        this.oxalisErrorRecognizer = oxalisErrorRecognizer;
    }

    @SuppressWarnings("ConstantConditions")
    public void send(@NotNull ContainerMessage cm) throws Exception {
        logger.info("Sending message: " + cm.toLog());

        try {
            PeppolSender sender = getPeppolSender(cm);
            if (sender == null) {
                logger.warn("PeppolSender is not initialized");
                return;
            }

            TransmissionResponse transmissionResponse = sender.send(cm);
            logger.info("Message " + cm.toLog() + " sent with transmission ID = " + transmissionResponse.getTransmissionIdentifier());
            cm.getProcessingInfo().setPeppolMessageMetadata(PeppolMessageMetadata.create(transmissionResponse));

        } catch (Exception e) {
            logger.warn("Sending of the message " + cm.toLog() + " failed with I/O error: " + e.getMessage());
            handleException(cm, e);
        }

        if (StringUtils.isNotBlank(cm.getProcessingInfo().getTransactionId())) {
            logger.debug("Message " + cm.toLog() + "delivered with transaction id = " + cm.getProcessingInfo().getTransactionId());
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

    @SuppressWarnings("ConstantConditions")
    private void handleException(@NotNull ContainerMessage cm, @NotNull Exception e) throws Exception {
        Endpoint endpoint = new Endpoint(componentName, ProcessType.OUT_OUTBOUND);
        cm.setStatus(endpoint, "message delivery failure");

        SendingErrors errorType = oxalisErrorRecognizer.recognize(e);

        if (errorType.isRetryable()) {
            String retry = cm.popRoute();
            if (StringUtils.isNotBlank(retry)) {
                cm.setStatus(new Endpoint(retry, ProcessType.OUT_PEPPOL_RETRY), "RETRY: " + retry);
                messageQueue.convertAndSend(retry, cm);
                logger.info("Message " + cm.toLog() + " queued for retry to the queue " + retry);
                return;
            }
            logger.info("No (more) retries possible for " + cm.toLog() + ", reporting IO error");
        } else {
            logger.info("Exception of type " + errorType + " registered as non-retriable, rejecting message " + cm.toLog());
        }

        cm.getProcessingInfo().setProcessingException(e.getMessage());

        if (errorType.requiresNotification()) {
            sendEmailNotification(cm, errorType);
        }
        if (errorType.requiresTicketCreation()) {
            createSNCTicket(cm, e);
        }
        reportException(cm, e);
    }

    private void sendEmailNotification(@NotNull ContainerMessage cm, @NotNull SendingErrors errorType) {
        try {
            logger.info("Sending message to email notificator since error: " + errorType + " requires notification");
            messageQueue.convertAndSend(emailNotificatorQueue, cm);
        } catch (Exception weird) {
            logger.error("Reporting to email-notificator threw exception: ", weird);
        }
    }

    private void createSNCTicket(@NotNull ContainerMessage cm, @NotNull Exception e) {
        try {
            errorHandler.reportWithContainerMessage(cm, e, StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Incident, UnknownError");
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
    }

    private void reportException(@NotNull ContainerMessage cm, @NotNull Exception e) {
        try {
            if (statusReporter != null) {
                statusReporter.reportError(cm, e);
            }
        } catch (Exception weird) {
            logger.error("Failed to report status using status reporter", weird);
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
