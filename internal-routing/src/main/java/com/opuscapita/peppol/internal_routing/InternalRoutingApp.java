package com.opuscapita.peppol.internal_routing;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.internal_routing.controller.RoutingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.internal_routing"})
@EnableDiscoveryClient
public class InternalRoutingApp {
    @Value("${peppol.internal-routing.queue.in.name}")
    private String queueName;

    @Value("${peppol.component.name}")
    private String componentName;

    public static void main(String[] args) {
        SpringApplication.run(InternalRoutingApp.class, args);
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler,
                                        @NotNull RoutingController controller, @NotNull MessageQueue messageQueue,
                                        @NotNull StatusReporter reporter) {
        return new AbstractQueueListener(errorHandler, reporter) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                cm = controller.loadRoute(cm);
                logger.info("Route set to " + cm.getRoute());

                String queueOut = cm.getRoute().pop();
                messageQueue.convertAndSend(queueOut, cm);
                cm.setStatus(componentName, "route set");
                logger.info("Route for " + cm.getFileName() + " defined, message sent to " + queueOut + " queue");
            }
        };
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

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
