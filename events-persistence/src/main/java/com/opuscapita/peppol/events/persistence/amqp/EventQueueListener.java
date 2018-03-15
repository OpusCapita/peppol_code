package com.opuscapita.peppol.events.persistence.amqp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.events.persistence.controller.PersistenceController;
import com.opuscapita.peppol.events.persistence.stats.Statistics;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by KALNIDA1 on 2016.07.12..
 */
@Component
public class EventQueueListener implements ChannelAwareMessageListener {
    private static final int CONNECTION_FAILURES_THRESHOLD = 5;
    private final AtomicInteger recentConnectionFailures = new AtomicInteger(0);
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
    public synchronized void receiveMessage(String data) throws Exception {

        long start = System.currentTimeMillis();
        String customerId = "n/a";
        try {
            PeppolEvent peppolEvent = deserializePeppolEvent(data);
            if (peppolEvent.getFileName() != null && !peppolEvent.getFileName().toLowerCase().endsWith("xml")) {
                logger.warn("Ignored event for non-data file: " + peppolEvent.getFileName());
                return;
            }
            customerId = peppolEvent.getProcessType().name().startsWith("IN") ? peppolEvent.getRecipientId() : peppolEvent.getSenderId();
//            retryTemplate.execute(new RetryCallback<Void, ConnectException>() {
//                @Override
//                public Void doWithRetry(RetryContext context) throws ConnectException {
//                    logger.info("Trying to store PEPPOL event, try " + context.getRetryCount() + ".");
            try {
                persistenceController.storePeppolEvent(peppolEvent);
            } catch (DataIntegrityViolationException e) {
                logger.warn("Retrying after 2 seconds");
                Thread.sleep(2000);
                persistenceController.storePeppolEvent(peppolEvent);
                logger.warn("Retry succeeded");
            }
            logger.info("Message about file: " + peppolEvent.getFileName() + " stored");
//                    return null;
//                }
//            });
            Statistics.updateLastSuccessful(start);
            recentConnectionFailures.set(0);
        } catch (Exception e) {
            if (e instanceof CannotCreateTransactionException) {
                throw e;
            } else {
                Statistics.updateLastFailed(start);
                handleError(data, customerId, e);
            }
        }

    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) throws Exception {
        receiveMessage(new String(data));
    }

    protected PeppolEvent deserializePeppolEvent(String message) {
        message = message.replace("\"urn\"", "urn"); //Sort of hack
        message = message.replaceAll("\\\\u003d", "=");  //Workaround for escaped = sign.
        message = message.replace("transportType", "processType");
        return gson.fromJson(message, PeppolEvent.class);
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            String fileName = extractFileNameFromMessage(message);
            if (fileName != null && !fileName.toLowerCase().endsWith("xml")) {
                logger.warn("Ignored event for non-data file: " + fileName);
                return;
            }
            errorHandler.reportWithoutContainerMessage(customerId, e, "Failed to persist event", fileName, fileName, message);
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

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("onMessage invoked");
        String data = new String(message.getBody());
        try {
            receiveMessage(data);
        } catch (Exception e) {
            logger.error(e.getClass().getCanonicalName());
            logger.warn("Failed to process message: " + data, e);
            boolean shouldRequeue = e instanceof CannotCreateTransactionException;
            if(shouldRequeue) {
                int connectionFailuresInRow = recentConnectionFailures.incrementAndGet();
                logger.info("There has been " + connectionFailuresInRow +" connection failure(s) in row");
                if(connectionFailuresInRow >= CONNECTION_FAILURES_THRESHOLD) {
                    logger.info("Sleeping for 30 seconds due to connection failures threshold exceeded");
                    Thread.sleep(30000);
                }
            }
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), shouldRequeue);
        }
    }
}
