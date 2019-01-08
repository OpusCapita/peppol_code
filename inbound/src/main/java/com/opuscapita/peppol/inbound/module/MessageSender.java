package com.opuscapita.peppol.inbound.module;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class MessageSender {
    private final static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Value("${peppol.inbound.queue.name}")
    private String queueName;

    @Value("${peppol.eventing.queue.in.name}")
    private String eventingQueue;

    private final MessageQueue messageQueue;
    private final ErrorHandler errorHandler;

    @Autowired
    public MessageSender(@NotNull MessageQueue messageQueue, @NotNull ErrorHandler errorHandler, @NotNull ApplicationContext ctx) {
        this.messageQueue = messageQueue;
        this.errorHandler = errorHandler;
    }

    // no exception must be thrown by this method
    // in case of a failure we have local file to reprocess
    void send(ContainerMessage cm) {
        try {
            logger.debug("Sending message to " + queueName + " about file: " + cm.getFileName());

            messageQueue.convertAndSend(queueName, cm);

            logger.info("Message sent to " + queueName + ", about file " + cm.toLog());
        } catch (Exception e) {
            logger.error("Failed to report received file " + cm.getFileName() + " to queue " + queueName, e);
            logger.error("Container message dump: " + cm.convertToJson());
            errorHandler.reportWithContainerMessage(cm, e, "Failed to report received file " + cm.getFileName() + " to queue " + queueName);
            return;
        }

        try {
            messageQueue.convertAndSend(eventingQueue, cm);
        } catch (Exception e) {
            logger.error("Failed to report received file " + cm.getFileName() + " status to " + eventingQueue + " queue");
            errorHandler.reportWithContainerMessage(cm, e, "Failed to report file " + cm.getFileName() + " status to queue " + queueName);
        }
    }

}
