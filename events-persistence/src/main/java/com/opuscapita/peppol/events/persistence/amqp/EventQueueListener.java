package com.opuscapita.peppol.events.persistence.amqp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.events.persistence.controller.PersistenceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by KALNIDA1 on 2016.07.12..
 */
@Component
public class EventQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(EventQueueListener.class);

    private final PersistenceController persistenceController;
    private final ErrorHandler errorHandler;
    private final Gson gson;
    // private final RetryTemplate retryTemplate;

    @Autowired
    public EventQueueListener(PersistenceController persistenceController, ErrorHandler errorHandler, Gson gson) {
        this.persistenceController = persistenceController;
        this.errorHandler = errorHandler;
        this.gson = gson;
        // this.retryTemplate = retryTemplate;
    }

    @SuppressWarnings("WeakerAccess")
    public synchronized void receiveMessage(String data) {
        String message = data.replace("\"urn\"", "urn"); //Sort of hack
        message = message.replaceAll("\\\\u003d", "=");  //Workaround for escaped = sign.
        String customerId = "n/a";
        try {
            PeppolEvent peppolEvent = deserializePeppolEvent(message);
            customerId = peppolEvent.getTransportType().name().startsWith("IN") ? peppolEvent.getRecipientId() : peppolEvent.getSenderId();
//            retryTemplate.execute(new RetryCallback<Void, ConnectException>() {
//                @Override
//                public Void doWithRetry(RetryContext context) throws ConnectException {
//                    logger.info("Trying to store PEPPOL event, try " + context.getRetryCount() + ".");
            persistenceController.storePeppolEvent(peppolEvent);
            logger.info("Message about file: " + peppolEvent.getFileName() + " stored");
//                    return null;
//                }
//            });
        } catch (Exception e) {
            logger.warn("Failed to process message", e);
            handleError(message, customerId, e);
        }

    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        receiveMessage(new String(data));
    }

    private PeppolEvent deserializePeppolEvent(String message) {
        return gson.fromJson(message, PeppolEvent.class);
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            errorHandler.reportToServiceNow(message, customerId, e, "Failed to persist event", extractFileNameFromMessage(message));
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }

    private String extractFileNameFromMessage(String message) {
        try {
            return new Gson().fromJson(message, JsonObject.class).get("fileName").getAsString();
        } catch (Exception e) {
            logger.info("No file name extracted for reporting error in message: " + message);
        }
        return "";
    }
}
