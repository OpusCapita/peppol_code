package com.opuscapita.peppol.validator.amqp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by KALNIDA1 on 2016.07.12..
 */
@Component
public class EventQueueListener {


    Logger logger = LoggerFactory.getLogger(EventQueueListener.class);


    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    Gson gson;

    @Autowired
    ValidationController validationController;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${peppol.eventing.queue.in.name}")
    String eventingQueueName;

    public synchronized void receiveMessage(@NotNull ContainerMessage containerMessage) {
        String customerId = containerMessage.getCustomerId();
        ValidationResult validationResult = validationController.validate(containerMessage);
        containerMessage.setValidationResult(validationResult);
        rabbitTemplate.convertAndSend(containerMessage.getRoute().pop(), containerMessage);
        try {
            // what? @SR
        } catch (Exception e) {
            String message = new String(containerMessage.getBytes());
            if (customerId == null) {
                customerId = populateCustomerIdWhenPossible(message);
            }
            handleError(message, customerId, e);
        }
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(@NotNull byte[] data) {
        String message = new String(data);
        System.out.println("*******************************************************");
        System.out.println(message);
        System.out.println("*******************************************************");
        ContainerMessage containerMessage = gson.fromJson(message, ContainerMessage.class);
        receiveMessage(containerMessage);
    }

    private String populateCustomerIdWhenPossible(String message) {
        String customerId = "n/a";
        try {
            JsonObject rawJsonMessage = new JsonParser().parse(message).getAsJsonObject();
            customerId = rawJsonMessage.get("transportType").getAsString().startsWith("IN") ? rawJsonMessage.get("receiverId").getAsString() : rawJsonMessage.get("senderId").getAsString();
        } catch (Exception e) {
            // do nothing
        }
        return customerId;
    }

    public void handleError(String message, String customerId, Exception e) {
        errorHandler.reportToServiceNow(message, customerId, e, "Validation failed exceptionally");
        errorHandler.reportFailureToAmqp(message, e, rabbitTemplate, eventingQueueName);
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
