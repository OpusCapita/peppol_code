package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.outbound", "eu.peppol.outbound.transmission"})
@EnableDiscoveryClient
@EnableAsync
public class OutboundApp {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(OutboundApp.class);

    private final OutboundController controller;

    @Value("${peppol.outbound.queue.in.name}")
    private String queueName;
    @Value("${peppol.component.name}")
    private String componentName;

    @Value("${peppol.outbound.consumers.default:2}")
    private int defaultConsumers;
    @Value("${peppol.outbound.consumers.max:4}")
    private int maxConsumers;
    @Value("${peppol.outbound.consumers.timeout.ms:60000}")
    private int consumersTimeout;

    @Autowired
    public OutboundApp(@NotNull @Lazy OutboundController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(OutboundApp.class, args);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(controller::send);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConcurrentConsumers(defaultConsumers > 0 ? defaultConsumers : 2);
        container.setMaxConcurrentConsumers(maxConsumers >= defaultConsumers ? maxConsumers : 4);
        container.setStopConsumerMinInterval(consumersTimeout);
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }


}
