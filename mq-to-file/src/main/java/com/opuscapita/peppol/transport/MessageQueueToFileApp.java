package com.opuscapita.peppol.transport;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.transport.controller.TransportController;
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

/**
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.transport"})
@EnableDiscoveryClient
public class MessageQueueToFileApp {

    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.mq-to-file.queue.in.name}")
    private String queueIn;

    public static void main(String[] args) {
        SpringApplication.run(MessageQueueToFileApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull StatusReporter reporter,
                                        @NotNull TransportController controller, @NotNull ContainerMessageSerializer serializer) {
        return new AbstractQueueListener(errorHandler, reporter, serializer) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.info("Storing incoming message: " + cm.getFileName());
                controller.storeMessage(cm);
                cm.setStatus(new Endpoint(componentName, ProcessType.IN_MQ_TO_FILE), "delivered");
            }
        };
    }

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
