package com.opuscapita.peppol.preprocessing;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.preprocessing.controller.PreprocessingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.preprocessing"})
public class PreprocessingApp {
    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.preprocessing.queue.in.name}")
    private String queueIn;
    @Value("${peppol.preprocessing.queue.out.name}")
    private String queueOut;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

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
    @NotNull
    MessageListenerAdapter listenerAdapter(@NotNull AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    @NotNull
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull StatusReporter reporter,
                                        @NotNull PreprocessingController controller, @NotNull RabbitTemplate rabbitTemplate) {
        return new AbstractQueueListener(errorHandler, reporter) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.info("Message received, file id: " + cm.getFileName());
                cm = controller.process(cm);
                cm.setStatus(componentName, "file read");
                if (cm.getBaseDocument() instanceof InvalidDocument) {
                    rabbitTemplate.convertAndSend(errorQueue, cm);
                } else {
                    rabbitTemplate.convertAndSend(queueOut, cm);
                }
                logger.info("Successfully processed and delivered to " + queueOut + " queue");
            }
        };
    }

}
