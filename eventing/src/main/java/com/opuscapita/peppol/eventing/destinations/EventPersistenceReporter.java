package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
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

        logger.info("Peppol event about " + cm.getFileName() + " successfully sent to " + queueOut + " queue");
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
            logger.warn("Failed to determine size for " + cm.getFileName());
        }
        result.setProcessType(endpoint.getType());

        if (cm.getDocumentInfo() == null) {
            cm.setDocumentInfo(new DocumentInfo());
        }
        result.setInvoiceId(cm.getDocumentInfo().getDocumentId());
        result.setDocumentType(cm.getDocumentInfo().getArchetype() + " " + cm.getDocumentInfo().getDocumentType());
        result.setInvoiceDate(cm.getDocumentInfo().getIssueDate());
        result.setDueDate(cm.getDocumentInfo().getDueDate());
        result.setRecipientId(cm.getDocumentInfo().getRecipientId());
        result.setSenderId(cm.getDocumentInfo().getSenderId());
        result.setRecipientName(cm.getDocumentInfo().getRecipientName());
        result.setSenderName(cm.getDocumentInfo().getSenderName());
        if (shouldExtractCommonNameFromMetadata(cm)) {
            cm.getProcessingInfo().setCommonName(extractCommonNameFromMetadata(cm));
        }
        result.setCommonName(cm.getProcessingInfo().getCommonName());
        result.setSenderCountryCode(cm.getDocumentInfo().getSenderCountryCode());
        result.setRecipientCountryCode(cm.getDocumentInfo().getRecipientCountryCode());
        result.setTransactionId(ps == null ? UUID.randomUUID().toString() : ps.getTransactionId());

        result.setErrorMessage(
                cm.getDocumentInfo().getErrors().stream().map(DocumentError::toString).collect(Collectors.joining(", ")));

        if(cm.getProcessingInfo().getProcessingException() != null) {
            String oldError = result.getErrorMessage() == null? "" : result.getErrorMessage();
            result.setErrorMessage(oldError + cm.getProcessingInfo().getProcessingException());
        }

        logger.debug("Peppol event prepared");
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean shouldExtractCommonNameFromMetadata(@NotNull ContainerMessage cm) {
        return (cm.getProcessingInfo().getCommonName() == null || !cm.getProcessingInfo().getCommonName().contains(","))
                && hasCommonNameInMetadata(cm);
    }


    @SuppressWarnings("ConstantConditions")
    private String extractCommonNameFromMetadata(ContainerMessage containerMessage) {
        String result = null;
        try {
            JsonObject rawMetadata = gson.fromJson(containerMessage.getProcessingInfo().getSourceMetadata(), JsonObject.class);
            PeppolMessageMetadata messageMetadata = gson.fromJson(rawMetadata.get("PeppolMessageMetaData"), PeppolMessageMetadata.class);
            result = containerMessage.isInbound() ? messageMetadata.getSendingAccessPointPrincipal() : messageMetadata.getReceivingAccessPoint();
            logger.info("Extracted Access Point Common Name [" + result + "] for file: " + containerMessage.getFileName());
        } catch (Exception e) {
            logger.warn("Failed to extract common name from metadata[" + containerMessage.getProcessingInfo().getSourceMetadata() + "] for file: " + containerMessage.getFileName());
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean hasCommonNameInMetadata(ContainerMessage containerMessage) {
        return (containerMessage.isInbound() && containerMessage.getProcessingInfo().getSourceMetadata().contains("sendingAccessPoint"))
                || (!containerMessage.isInbound() && containerMessage.getProcessingInfo().getSourceMetadata().contains("receivingAccessPoint"));
    }
}
