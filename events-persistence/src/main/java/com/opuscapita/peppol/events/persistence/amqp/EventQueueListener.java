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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

/**
 * Created by KALNIDA1 on 2016.07.12..
 */
@Component
public class EventQueueListener {


    Logger logger = LoggerFactory.getLogger(EventQueueListener.class);

    @Autowired
    PersistenceController persistenceController;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    Gson gson;


    @Autowired
    private RetryTemplate retryTemplate;

    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data).replace("\"urn\"", "urn"); //Sort of hack
        String customerId = "n/a";
        try {
            PeppolEvent peppolEvent = deserializePeppolEvent(message);
            customerId = peppolEvent.getTransportType().name().startsWith("IN") ? peppolEvent.getRecipientId() : peppolEvent.getSenderId();
            retryTemplate.execute(new RetryCallback<Void, ConnectException>() {
                @Override
                public Void doWithRetry(RetryContext context) throws ConnectException {
                    logger.info("Trying to store PEPPOL event, try " + context.getRetryCount() + ".");
                    persistenceController.storePeppolEvent(peppolEvent);
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handleError(message, customerId, e);
        }
    }

    private PeppolEvent deserializePeppolEvent(String message) {
        return gson.fromJson(message, PeppolEvent.class);
    }

    public void handleError(String message, String customerId, Exception e) {
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
