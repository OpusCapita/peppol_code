package com.opuscapita.peppol.preprocessing.amqp;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.preprocessing.controller.PreprocessingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class PreprocessingQueueListener {
    private final static Logger logger = LoggerFactory.getLogger(PreprocessingQueueListener.class);

    @Value("${amqp.queue.out.name}")
    private String queueName;

    private final PreprocessingController controller;
    private final ErrorHandler errorHandler;
    private final RabbitTemplate rabbitTemplate;
    private final Gson gson;

    @Autowired
    public PreprocessingQueueListener(@Nullable ErrorHandler errorHandler, @NotNull PreprocessingController controller,
                                      @NotNull RabbitTemplate rabbitTemplate, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.controller = controller;
        this.rabbitTemplate = rabbitTemplate;
        this.gson = gson;
    }

    @SuppressWarnings("unused")
    public synchronized void receiveMessage(byte[] data) {
        ContainerMessage input = gson.fromJson(new String(data), ContainerMessage.class);

        try {
            ContainerMessage result = controller.process(input);
            rabbitTemplate.convertAndSend(queueName, result);
            logger.info("Successfully processed incoming message: " + input.getFileName());
        } catch (Exception e) {
            logger.error("Failed to process preprocessing file: " + input.getFileName(), e);
            handleError("Failed to process preprocessing file: " + input.getFileName(), "n/a", e);
        }
    }

    private void handleError(String message, String customerId, Exception e) {
        try {
            if (errorHandler != null) {
                errorHandler.reportToServiceNow(message, customerId, e, "Failed to process preprocessing file");
            }
            logger.error(message + ", customerId: " + customerId, e);
        } catch (Exception weird) {
            logger.error("Reporting to ServiceNow threw exception: ", weird);
        }
        throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
    }
}
