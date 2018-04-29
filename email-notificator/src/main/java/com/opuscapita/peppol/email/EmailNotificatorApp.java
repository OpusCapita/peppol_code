package com.opuscapita.peppol.email;

import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import com.opuscapita.peppol.email.controller.EmailController;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.email"})
@EnableScheduling
@EnableDiscoveryClient
public class EmailNotificatorApp {
    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String queueName;

    private final EmailController controller;

    @Autowired
    public EmailNotificatorApp(@NotNull @Lazy EmailController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmailNotificatorApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    /*
        1. Controller stores message in files or appends to existing files for this recipient.
        2. Sender checks for files and sends those out, then deletes files.
     */
    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(controller::processMessage);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
