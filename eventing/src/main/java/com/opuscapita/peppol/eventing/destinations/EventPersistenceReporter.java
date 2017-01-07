package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.TransportType;
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
import org.springframework.core.env.Environment;
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
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;
    private final Environment environment;
    @Value("${peppol.eventing.queue.out.name}")
    private String queueOut;

    @Autowired
    public EventPersistenceReporter(@NotNull Environment environment, @NotNull RabbitTemplate rabbitTemplate, @NotNull Gson gson) {
        this.environment = environment;
        this.rabbitTemplate = rabbitTemplate;
        this.gson = gson;
    }

    public void process(@NotNull ContainerMessage cm) {
        PeppolEvent event = convert(cm);

        if (event == null) {
            logger.debug("Ignoring source " + cm.getProcessingStatus().getComponentName());
            return;
        }
        String result = gson.toJson(event);
        rabbitTemplate.convertAndSend(queueOut, result);

        logger.info("Peppol event about " + cm.getFileName() + " successfully sent to " + queueOut + " queue");
    }

    @Nullable
    private PeppolEvent convert(@NotNull ContainerMessage cm) {
        logger.debug("Message received");

        ProcessingStatus ps = cm.getProcessingStatus();
        String typeDefinition = environment.getProperty("peppol.eventing.transport.type." + ps.getComponentName());
        if (typeDefinition == null) {
            return null;
        }

        TransportType transportType;
        try {
            transportType = TransportType.valueOf(typeDefinition);
        } catch (IllegalArgumentException e) {
            logger.error("Unknown transport type for peppol.eventing.transport.type." + ps.getComponentName() + " = " + typeDefinition +
                    ". Message ignored");
            return null;
        }

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
            result.setTransportType(transportType);

            if (cm.getBaseDocument() instanceof InvalidDocument) {
                InvalidDocument invalid = (InvalidDocument) cm.getBaseDocument();
                result.setErrorMessage(invalid.getReason());
            }
        }

        logger.debug("Peppol event prepared");
        return result;
    }

}
