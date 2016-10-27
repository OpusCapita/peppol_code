package com.opuscapita.peppol.events.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.commons.model.util.PeppolEventDeSerializer;
import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.opuscapita.peppol.events.persistence.model")
@ComponentScan(basePackages = {"com.opuscapita.peppol.events.persistence", "com.opuscapita.peppol.events.persistence.model", "com.opuscapita.peppol.commons.model"})
@EntityScan(basePackages = {"com.opuscapita.peppol.events.persistence.model", "com.opuscapita.peppol.commons.model"})
@EnableTransactionManagement
@EnableRetry
public class EventsPersistenceApplication {
    @Value("${amqp.queueName}")
    private String queueName;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EventsPersistenceApplication.class, args);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(10000); // 10 seconds

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        return template;
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
