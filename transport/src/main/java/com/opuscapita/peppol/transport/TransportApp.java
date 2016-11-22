package com.opuscapita.peppol.transport;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.TransportType;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.transport.contoller.TransportController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application that stores files to gateways and checks incoming files from gateways.
 * Can be used for local directories too.
 *
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.transport"})
@EnableScheduling
public class TransportApp {
    @Value("${peppol.transport.queue.in.name}")
    private String queueIn;

    public static void main(String[] args) {
        SpringApplication.run(TransportApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnProperty({"spring.rabbitmq.host", "peppol.transport.queue.in.enabled"})
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    @ConditionalOnProperty("peppol.transport.queue.in.enabled")
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull StatusReporter reporter, @NotNull TransportController controller) {
        return new AbstractQueueListener(errorHandler, reporter) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.debug("Storing incoming message: " + cm.getFileName());
                controller.storeMessage(cm);
                cm.setStatus(TransportType.IN_OUT, "delivered");
            }
        };
    }

    @Bean
    @ConditionalOnProperty("peppol.transport.queue.in.enabled")
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
