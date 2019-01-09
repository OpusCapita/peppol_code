package com.opuscapita.peppol.commons.events;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.EndpointUtil;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;
import java.util.UUID;

public class EventingMessageUtil {
    protected final static Logger logger = LoggerFactory.getLogger(EventingMessageUtil.class);

    public static String generateMessageId(ContainerMessage containerMessage) {
        String result = UUID.randomUUID().toString();
        try {
            if (containerMessage.isInbound()) {
                result = containerMessage.getProcessingInfo() == null ? extractBaseName(containerMessage.getOriginalFileName()) : containerMessage.getProcessingInfo().getTransactionId();
            } else {
                result = extractBaseName(containerMessage.getOriginalFileName());
                if (containerMessage.getDocumentInfo() == null) {
                    result = result + "_" + containerMessage.getProcessingInfo().getSource().getName();
                } else if (containerMessage.getCustomerId() == null || containerMessage.getDocumentInfo().getDocumentId().isEmpty()) {
                    result = result + "_" + containerMessage.getCustomerId() + " " + containerMessage.getDocumentInfo().getDocumentId();
                } else {
                    result = result + "_invalid_" + UUID.randomUUID().toString();
                }
            }
        } catch (Exception e) {
            logger.error("Due to failure generated random message id for: " + containerMessage.getOriginalFileName());
            e.printStackTrace();
        }
        String prefix = containerMessage.isInbound() ? "IN_" : "OUT_";
        return prefix + result;
    }

    protected static String extractBaseName(String fullPath) {
        return FilenameUtils.getName(fullPath);
    }

    public static void reportEvent(ContainerMessage containerMessage, String details) {
        if (containerMessage.getProcessingInfo() == null) {
            logger.error("No processing info for: " + containerMessage);
            // FIXME NullPointerException imminent
        }

        boolean shouldCreateEventingMessage = containerMessage.getProcessingInfo().getEventingMessage() == null;
        logger.debug("Should create eventing message: " + shouldCreateEventingMessage);
        Message message = shouldCreateEventingMessage ? createAndSetMessage(containerMessage, details) : containerMessage.getProcessingInfo().getEventingMessage();
        logger.debug("Eventing message: " + message);
        message.getAttempts().last().getEvents().add(createEvent(containerMessage, details));
    }

    protected static Message createAndSetMessage(ContainerMessage containerMessage, String details) {
        long created = System.currentTimeMillis();
        Message message = new Message(
                generateMessageId(containerMessage),
                created,
                containerMessage.isInbound(), new TreeSet<Attempt>() {{
            add(createAttempt(containerMessage, details));
        }}
        );
        containerMessage.getProcessingInfo().setEventingMessage(message);
        return message;
    }

    protected static Attempt createAttempt(ContainerMessage containerMessage, String details) {

        return new Attempt(
                System.currentTimeMillis() + "_" + FilenameUtils.getName(containerMessage.getFileName()),
                new TreeSet<Event>() {{
                    add(createEvent(containerMessage, details));
                }},
                containerMessage.getFileName()
        );
    }

    protected static Event createEvent(@NotNull ContainerMessage containerMessage, @NotNull String details) {
        Endpoint endpoint = containerMessage.getProcessingInfo().getCurrentEndpoint() == null ? containerMessage.getProcessingInfo().getSource() : containerMessage.getProcessingInfo().getCurrentEndpoint();
        return new Event(
                System.currentTimeMillis(),
                endpoint.getName(),
                containerMessage.getProcessingInfo().getCurrentStatus(),
                calculateIfTerminalStatus(containerMessage),
                details
        );
    }

    protected static boolean calculateIfTerminalStatus(ContainerMessage containerMessage) {
        boolean result = false;
        //Positive case of terminal status determination
        try {
            if (EndpointUtil.isTerminal(containerMessage.getProcessingInfo().getCurrentEndpoint())) {
                result = true;
            }
        } catch (NullPointerException e) {
            logger.warn("No current endpoint set for: " + containerMessage.toLog());
        }


        //Negative cases of terminal status determination
        if (containerMessage.hasErrors()) {
            result = true;
        }
        if (containerMessage.getDocumentInfo() != null
                && (containerMessage.getDocumentInfo().getArchetype() == Archetype.INVALID || containerMessage.getDocumentInfo().getArchetype() == Archetype.UNRECOGNIZED)) {
            result = true;
        }
        return result;
    }

    public static void updateEventingInformation(ContainerMessage containerMessage) {
        if(containerMessage.getProcessingInfo() != null) {
            Message message = containerMessage.getProcessingInfo().getEventingMessage();
            if(message != null) {
                message.getAttempts().last().setFileName(containerMessage.getFileName());
                message.setDocumentType(containerMessage.getDocumentInfo().getDocumentType());
                message.setDocumentNumber(containerMessage.getDocumentInfo().getDocumentId());
                message.setDocumentDate(containerMessage.getDocumentInfo().getIssueDate());
                message.setDueDate(containerMessage.getDocumentInfo().getDueDate());
            }
        }
    }
}
