package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
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

import java.io.UnsupportedEncodingException;
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
    private PeppolEvent convert(@NotNull ContainerMessage cm) {
        logger.debug("Message received");

        ProcessingInfo ps = cm.getProcessingInfo();
        Endpoint endpoint = ps == null ? new Endpoint("error", ProcessType.UNKNOWN) : ps.getCurrentEndpoint();

        PeppolEvent result = new PeppolEvent();
        result.setFileName(cm.getFileName());
        result.setProcessType(endpoint.getType());

        if (cm.getDocumentInfo() == null) {
            cm.setDocumentInfo(new DocumentInfo());
        }
        result.setInvoiceId(cm.getDocumentInfo().getDocumentId());
        logger.debug("DEBUG##: received document type: " + cm.getDocumentInfo().getDocumentType());
        result.setDocumentType(cm.getDocumentInfo().getDocumentType());
        result.setInvoiceDate(cm.getDocumentInfo().getIssueDate());
        result.setDueDate(cm.getDocumentInfo().getDueDate());
        result.setRecipientId(cm.getDocumentInfo().getRecipientId());
        result.setSenderId(cm.getDocumentInfo().getSenderId());
        result.setRecipientName(cm.getDocumentInfo().getRecipientName());
        result.setSenderName(cm.getDocumentInfo().getSenderName());
        result.setSenderCountryCode(cm.getDocumentInfo().getSenderCountryCode());
        result.setRecipientCountryCode(cm.getDocumentInfo().getRecipientCountryCode());
        result.setTransactionId(ps == null ? UUID.randomUUID().toString() : ps.getTransactionId());

        result.setErrorMessage(
                cm.getDocumentInfo().getErrors().stream().map(DocumentError::toString).collect(Collectors.joining(", ")));

        logger.debug("Peppol event prepared");
        return result;
    }

}
