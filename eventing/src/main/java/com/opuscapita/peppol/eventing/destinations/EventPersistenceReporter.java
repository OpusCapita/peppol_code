package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.ApInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Gets data from inside the container message and sends it as a events-persistence event.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EventPersistenceReporter {
    private final static Logger logger = LoggerFactory.getLogger(EventPersistenceReporter.class);
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;

    @Value("${peppol.eventing.queue.out.name}")
    private String queueOut;

    @Autowired
    public EventPersistenceReporter(@NotNull RabbitTemplate rabbitTemplate, @NotNull Gson gson) {
        this.rabbitTemplate = rabbitTemplate;
        this.gson = gson;
    }

    public void process(@NotNull ContainerMessage cm) {
        PeppolEvent event = convert(cm);

        String result = gson.toJson(event);
        try {
            rabbitTemplate.convertAndSend(queueOut, result.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        logger.info("Peppol event sent to " + queueOut + " queue, about the message: " + cm.toLog());
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    PeppolEvent convert(@NotNull ContainerMessage cm) {
        logger.debug("Message received");

        ProcessingInfo ps = cm.getProcessingInfo();
        Endpoint endpoint = ps == null ? new Endpoint("error", ProcessType.UNKNOWN) : ps.getCurrentEndpoint();

        PeppolEvent result = new PeppolEvent();
        result.setFileName(cm.getFileName());
        try {
            result.setFileSize(Files.size(Paths.get(cm.getFileName())));
        } catch (IOException e) {
            logger.warn("Failed to determine size for " + cm.toLog());
        }
        result.setProcessType(endpoint.getType());

        if (cm.getDocumentInfo() == null) {
            cm.setDocumentInfo(new DocumentInfo());
        }
        if (isNotReprocess(cm)) {
            result.setOriginalSource(cm.getProcessingInfo().getOriginalSource());
        }
        result.setInvoiceId(cm.getDocumentInfo().getDocumentId());
        result.setDocumentType(cm.getDocumentInfo().getArchetype() + " " + cm.getDocumentInfo().getDocumentType());
        result.setInvoiceDate(cm.getDocumentInfo().getIssueDate());
        result.setDueDate(cm.getDocumentInfo().getDueDate());
        result.setRecipientId(cm.getDocumentInfo().getRecipientId());
        result.setSenderId(cm.getDocumentInfo().getSenderId());
        result.setRecipientName(cm.getDocumentInfo().getRecipientName());
        result.setSenderName(cm.getDocumentInfo().getSenderName());
        result.setCommonName(extractCommonNameFromMetadata(cm));
        result.setSenderCountryCode(cm.getDocumentInfo().getSenderCountryCode());
        result.setRecipientCountryCode(cm.getDocumentInfo().getRecipientCountryCode());
        result.setTransactionId(ps == null ? UUID.randomUUID().toString() : ps.getTransactionId());

        result.setErrorMessage(
                cm.getDocumentInfo().getErrors().stream().map(DocumentError::toString).collect(Collectors.joining(", ")));

        if (cm.getProcessingInfo().getProcessingException() != null) {
            String oldError = result.getErrorMessage() == null ? "" : result.getErrorMessage();
            result.setErrorMessage(oldError + cm.getProcessingInfo().getProcessingException());
        }

        logger.debug("Peppol event prepared");
        return result;
    }

    private boolean isNotReprocess(@NotNull ContainerMessage cm) {
        return cm.getProcessingInfo().getSource().getType() != ProcessType.OUT_REPROCESS && cm.getProcessingInfo().getSource().getType() != ProcessType.IN_REPROCESS;
    }

    private String extractCommonNameFromMetadata(ContainerMessage cm) {
        if (cm.getProcessingInfo() == null || cm.getProcessingInfo().getApInfo() == null) {
            return null;
        }
        ApInfo apInfo = cm.getProcessingInfo().getApInfo();
        logger.info("Extracted Access Point Common Name [" + apInfo + "] from message: " + cm.toLog());
        return apInfo.toString();
    }
}
