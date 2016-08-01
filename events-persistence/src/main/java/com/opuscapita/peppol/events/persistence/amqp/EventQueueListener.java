package com.opuscapita.peppol.events.persistence.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.events.persistence.controller.PersistanceController;
import com.opuscapita.peppol.events.persistence.errors.ErrorHandler;
import com.opuscapita.peppol.events.persistence.model.*;
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
    PersistanceController persistanceController;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    Gson gson;

    public synchronized void receiveMessage(byte[] data) {
        String message = new String(data);
        System.out.println("*******************************************************");
        System.out.println(message);
        System.out.println("*******************************************************");
        try {
            PeppolEvent peppolEvent = gson.fromJson(message, PeppolEvent.class);
            persistanceController.storePeppolEvent(peppolEvent);
        } catch (Exception e) {
            e.printStackTrace();
            handleError(message, e);
        }
    }

    public void handleError(String message, Exception e) {
        errorHandler.logBadMessage(message, e);
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
