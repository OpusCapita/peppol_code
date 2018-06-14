package com.opuscapita.peppol.preprocessing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import com.opuscapita.peppol.preprocessing.controller.PreprocessingController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.preprocessing"})
@EnableDiscoveryClient
public class PreprocessingApp {
    private static final Logger logger = LoggerFactory.getLogger(PreprocessingApp.class);

    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.preprocessing.queue.in.name}")
    private String queueIn;
    @Value("${peppol.preprocessing.queue.out.name}")
    private String queueOut;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    private final PreprocessingController controller;
    private final ErrorHandler errorHandler;
    private final MessageQueue messageQueue;

    @Autowired
    public PreprocessingApp(@Lazy @NotNull PreprocessingController controller, @NotNull ErrorHandler errorHandler, @NotNull MessageQueue messageQueue) {
        this.controller = controller;
        this.errorHandler = errorHandler;
        this.messageQueue = messageQueue;
    }

    public static void main(String[] args) {
        SpringApplication.run(PreprocessingApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageConverter(new JsonMessageConverter());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(this::consume);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    private void consume(@NotNull ContainerMessage cm) throws Exception {
        logger.info("Message received, file id: " + cm.getFileName());
        cm = controller.process(cm);

        if (cm.getProcessingInfo() == null) {
            throw new IllegalStateException("Processing info is missing from ContainerMessage for: " + cm.getFileName());
        }

        if (cm.getDocumentInfo() != null && cm.getDocumentInfo().getArchetype() == Archetype.UNRECOGNIZED) {
            String fileName = logFileErrors(cm);
            errorHandler.reportWithContainerMessage(cm, null, "Document not recognized by the parser in: " + fileName);
            cm.setStatus(cm.getProcessingInfo().getCurrentEndpoint(), "invalid file, document type unrecognized");

        } else if (cm.getDocumentInfo() == null || cm.getDocumentInfo().getArchetype() == Archetype.INVALID) {
            if(cm.getDocumentInfo() != null) {
                logFileErrors(cm);
            }
            cm.setStatus(cm.getProcessingInfo().getCurrentEndpoint(), "invalid file");
            EventingMessageUtil.reportEvent(cm, "Invalid file");
            messageQueue.convertAndSend(errorQueue, cm);
            logger.info("Invalid message sent to " + errorQueue + " queue");
        } else {
            EventingMessageUtil.reportEvent(cm, "Preprocessing complete, sent to: " + queueOut);
            EventingMessageUtil.updateEventingInformation(cm);
            messageQueue.convertAndSend(queueOut, cm);
            logger.info("Successfully processed and delivered to " + queueOut + " queue");
        }
    }

    @NotNull
    private String logFileErrors(@NotNull ContainerMessage cm) {
        String fileName = new File(cm.getFileName()).getName();
        String errorsSoFar = cm.getDocumentInfo().getErrors().stream().map(DocumentError::toString).collect(Collectors.joining("\n\r"));
        logger.warn("Document type recognition for " + fileName + " failed with following errors: " + errorsSoFar);
        return fileName;
    }

}
