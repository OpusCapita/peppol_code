package com.opuscapita.peppol.transport;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.transport.amqp.TransportQueueListener;
import com.opuscapita.peppol.transport.checker.IncomingChecker;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application that stores files to gateways and checks incoming files from gateways.
 * Can be used for local directories too.
 *
 * @author Sergejs.Roze
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@EnableScheduling
@ComponentScan({"com.opuscapita.peppol.transport", "com.opuscapita.peppol.commons.storage", "com.opuscapita.peppol.commons.container",
    "com.opuscapita.peppol.commons.errors", "com.opuscapita.commons"})
public class TransportApp {
    @Value("${amqp.queue.in.name}")
    private String queueName;

    private final Environment environment;

    @Autowired
    public TransportApp(@NotNull Environment environment, @NotNull ApplicationContext context) {
        this.environment = environment;
        context.getBean(IncomingChecker.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TransportApp.class, args);
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
    @ConditionalOnProperty("amqp.queue.in.enabled")
    MessageListenerAdapter listenerAdapter(TransportQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    Gson gson() {
        return new Gson();
    }

    @Bean
    @ConditionalOnProperty("snc.enabled")
    ErrorHandler errorHandler() {
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
    @ConditionalOnProperty("snc.enabled")
    ServiceNow serviceNowRest() {
        return new ServiceNowREST(serviceNowConfiguration());
    }
}
