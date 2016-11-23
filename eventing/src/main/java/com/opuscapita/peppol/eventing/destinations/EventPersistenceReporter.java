package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Gets data from inside the container message and sends it as a events-persistence event.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EventPersistenceReporter {
    private final static Logger logger = LoggerFactory.getLogger(EventPersistenceReporter.class);

    @Value("${peppol.eventing.queue.out.name}")
    private String queueOut;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventPersistenceReporter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void process(@NotNull ContainerMessage cm) {
        PeppolEvent event = convert(cm);
        rabbitTemplate.convertAndSend(queueOut, event);
        logger.info("Peppol event successfully sent to " + queueOut + " queue");
    }

    @NotNull
    private PeppolEvent convert(@NotNull ContainerMessage cm) {
        logger.debug("Message received");

        PeppolEvent result = new PeppolEvent();
        result.setFileName(cm.getFileName());

        if (cm.getBaseDocument() != null) {
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

            // result.setTransportType(cm.getProcessingStatus().getTransportType()); TODO

            if (cm.getBaseDocument() instanceof InvalidDocument) {
                InvalidDocument invalid = (InvalidDocument) cm.getBaseDocument();
                result.setErrorMessage(invalid.getReason());
            }
        }

        logger.debug("Peppol event prepared");
        return result;
    }

}