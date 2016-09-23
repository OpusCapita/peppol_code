package com.opuscapita.peppol.events.persistence.amqp;

import com.google.gson.Gson;
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


    Logger logger = LoggerFactory.getLogger(EventQueueListener.class);

    @Autowired
    PersistenceController persistenceController;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    Gson gson;

    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data).replace("\"urn\"", "urn"); //Sort of hack
        String customerId = "n/a";
        try {
            PeppolEvent peppolEvent = gson.fromJson(message, PeppolEvent.class);
            customerId = peppolEvent.getTransportType().name().startsWith("IN") ? peppolEvent.getRecipientId() : peppolEvent.getSenderId();
            persistenceController.storePeppolEvent(peppolEvent);
        } catch (Exception e) {
            e.printStackTrace();
            handleError(message, customerId, e);
        }
    }

    public void handleError(String message, String customerId, Exception e) {
        try {
            errorHandler.reportToServiceNow(message, customerId, e, "Failed to persist event");
        } catch (Exception wierd) {
            logger.error("reporting to service now threw exception: ");
            wierd.printStackTrace();
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
