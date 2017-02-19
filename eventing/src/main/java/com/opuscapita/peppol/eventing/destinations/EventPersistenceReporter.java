package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.status.ProcessingStatus;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

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

        if (event == null) {
            return;
        }
        String result = gson.toJson(event);
        try {
            rabbitTemplate.convertAndSend(queueOut, result.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        logger.info("Peppol event about " + cm.getFileName() + " successfully sent to " + queueOut + " queue");
    }

    @Nullable
    private PeppolEvent convert(@NotNull ContainerMessage cm) {
        logger.debug("Message received");

        if (cm.getBaseDocument() == null) {
            logger.info("No document in received message, ignoring message");
            return null;
        }

        ProcessingStatus ps = cm.getProcessingStatus();
        Endpoint endpoint = ps.getEndpoint();

        PeppolEvent result = new PeppolEvent();
        result.setFileName(cm.getFileName());
        result.setProcessType(endpoint.getType());

        result.setInvoiceId(cm.getBaseDocument().getDocumentId());
        result.setInvoiceDate(cm.getBaseDocument().getIssueDate());
        result.setDueDate(cm.getBaseDocument().getDueDate());
        result.setRecipientId(cm.getBaseDocument().getRecipientId());
        result.setSenderId(cm.getBaseDocument().getSenderId());
        result.setRecipientName(cm.getBaseDocument().getRecipientName());
        result.setSenderName(cm.getBaseDocument().getSenderName());
        result.setSenderCountryCode(cm.getBaseDocument().getSenderCountryCode());
        result.setRecipientCountryCode(cm.getBaseDocument().getRecipientCountryCode());
        result.setTransactionId(cm.getTransactionId());

        if (cm.getBaseDocument() instanceof InvalidDocument) {
            InvalidDocument invalid = (InvalidDocument) cm.getBaseDocument();
            result.setErrorMessage(invalid.getReason());
        }
        if (cm.getValidationResult() != null && cm.getValidationResult().getErrors() != null &&
                cm.getValidationResult().getErrors().size() > 0) {
            result.setErrorMessage(cm.getValidationResult().getErrorsString());
        }

        logger.debug("Peppol event prepared");
        return result;
    }

}
