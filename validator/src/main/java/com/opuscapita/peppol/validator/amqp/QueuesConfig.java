package com.opuscapita.peppol.validator.amqp;

import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by bambr on 16.9.8.
 */
@Configuration
@RefreshScope
public class QueuesConfig {
    @Value("${peppol.validation.consume-queue}")
    String incomingQueueName;

    /*@Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        return getMessageContainerForQueue(connectionFactory, listenerAdapter);
    }*/

    @Bean
    public SimpleMessageListenerContainer incomingQueueContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        return getMessageContainerForQueue(connectionFactory, listenerAdapter, incomingQueueName);

    }



    @NotNull
    private SimpleMessageListenerContainer getMessageContainerForQueue(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(EventQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
