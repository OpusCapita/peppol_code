package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.outbound", "eu.peppol.outbound.transmission"})
@EnableDiscoveryClient
public class OutboundApp {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(OutboundApp.class);

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

    public static void main(String[] args) {
        SpringApplication.run(OutboundApp.class, args);
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler,
                                        @NotNull OutboundController controller, @NotNull StatusReporter reporter,
                                        @NotNull ContainerMessageSerializer serializer) {
        return new AbstractQueueListener(errorHandler, reporter, serializer) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                controller.send(cm);
                if (StringUtils.isNotBlank(cm.getProcessingInfo().getTransactionId())) {
                    logger.debug("Message " + cm.getFileName() + "delivered with transaction id = " + cm.getProcessingInfo().getTransactionId());
                    cm.setStatus(new Endpoint(componentName, ProcessType.OUT_OUTBOUND), "delivered");
                }
            }
        };
    }

    @Bean
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
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
