package com.opuscapita.peppol.events.persistence;

import com.google.gson.*;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import com.opuscapita.peppol.events.persistence.model.PeppolEvent;
import com.opuscapita.peppol.events.persistence.model.TransportType;
import com.opuscapita.peppol.events.persistence.model.util.PeppolEventDeSerializer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Type;

@SpringBootApplication
public class EventsPersistanceApplication {
    @Value("${amqp.queueName}")
    private String queueName;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EventsPersistanceApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(EventQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().registerTypeAdapter(PeppolEvent.class, new PeppolEventDeSerializer()).create();
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    ServiceNowConfiguration serviceNowConfiguration() {
        return new ServiceNowConfiguration(
                environment.getProperty("snc.rest.url"),
                environment.getProperty("snc.rest.username"),
                environment.getProperty("snc.rest.password"),
                environment.getProperty("snc.bsc"),
                environment.getProperty("snc.from"),
                environment.getProperty("snc.businessGroup"));
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNowREST(serviceNowConfiguration());
    }
}
