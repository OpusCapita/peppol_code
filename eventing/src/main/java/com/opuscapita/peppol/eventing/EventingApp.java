package com.opuscapita.peppol.eventing;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.eventing.controller.ContainerMessageToPeppolEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Gets messages from different modules and produces messages for events-persistence.
 *
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.eventing"})
public class EventingApp {
    @Value("${peppol.eventing.queue.in.name}")
    private String queueIn;
    @Value("${peppol.eventing.queue.out.name}")
    private String queueOut;

    public static void main(String[] args) {
        SpringApplication.run(EventingApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(AbstractQueueListener queueListener) {
        return new MessageListenerAdapter(queueListener, "receiveMessage");
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull Gson gson,
                                        @NotNull ContainerMessageToPeppolEvent controller, @NotNull RabbitTemplate rabbitTemplate) {
        return new AbstractQueueListener(errorHandler, null, gson) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                PeppolEvent event = controller.process(cm);
                rabbitTemplate.convertAndSend(queueOut, event);
                logger.debug("Peppol event successfully sent");
            }
        };
    }

}
