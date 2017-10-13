package com.opuscapita.peppol.commons.events;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
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
                result = containerMessage.getProcessingInfo() == null ? FilenameUtils.getBaseName(containerMessage.getOriginalFileName()) : containerMessage.getProcessingInfo().getTransactionId();
            } else {
                result = FilenameUtils.getBaseName(containerMessage.getOriginalFileName());
                if (containerMessage.getCustomerId() == null || containerMessage.getDocumentInfo().getDocumentId().isEmpty()) {
                    result = result + "_" + containerMessage.getCustomerId() + " " + containerMessage.getDocumentInfo().getDocumentId();
                } else {
                    result = result + "_invalid_" + UUID.randomUUID().toString();
                }
            }
        } catch (Exception e) {
            logger.error("Due to failure generated random message id for: " + containerMessage.getOriginalFileName());
            e.printStackTrace();
        }
        return result;
    }

    public static void reportEvent(ContainerMessage containerMessage, String details) {
        Message message = containerMessage.getProcessingInfo().getEventingMessage() == null ? createMessage(containerMessage, details) : containerMessage.getProcessingInfo().getEventingMessage();
        message.getAttempts().last().getEvents().add(createEvent(containerMessage, details));
    }

    protected static Message createMessage(ContainerMessage containerMessage, String details) {
        long created = System.currentTimeMillis();
        Message message = new Message(
                generateMessageId(containerMessage),
                created,
                new TreeSet<Attempt>() {{
                    add(createAttempt(containerMessage, details));
                }}
        );
        containerMessage.getProcessingInfo().setEventingMessage(message);
        return null;
    }

    protected static Attempt createAttempt(ContainerMessage containerMessage, String details) {
        return new Attempt(
                System.currentTimeMillis(),
                new TreeSet<Event>() {{
                    add(createEvent(containerMessage, details));
                }},
                containerMessage.getFileName()
        );
    }

    protected static Event createEvent(@NotNull ContainerMessage containerMessage, @NotNull String details) {
        return new Event(
                System.currentTimeMillis(),
                containerMessage.getProcessingInfo().getCurrentEndpoint().getName(),
                containerMessage.getProcessingInfo().getCurrentStatus(),
                calculateIfTerminalStatus(containerMessage),
                details
        );
    }

    protected static boolean calculateIfTerminalStatus(ContainerMessage containerMessage) {
        boolean result = false;
        //Positive case of terminal status determination
        if(EndpointUtil.isTerminal(containerMessage.getProcessingInfo().getCurrentEndpoint())) {
            result = true;
        }

        //Negative cases of terminal status determination
        if(containerMessage.hasErrors()) {
            result = true;
        }
        if(containerMessage.getDocumentInfo().getArchetype() == Archetype.INVALID) {
            result = true;
        }
        return result;
    }
}
