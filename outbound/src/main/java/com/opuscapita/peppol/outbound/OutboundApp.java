package com.opuscapita.peppol.outbound;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.outbound.amqp.OutboundQueueListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author Sergejs.Roze
 */
public class OutboundApp {
    @Value("${amqp.queue.in.name}")
    private String queueName;

    private final Environment environment;

    @Autowired
    public OutboundApp(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(OutboundApp.class, args);
    }

    @SuppressWarnings("Duplicates")
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
    public Gson gson() {
        return new Gson();
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(OutboundQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    @ConditionalOnProperty("snc.enabled")
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
