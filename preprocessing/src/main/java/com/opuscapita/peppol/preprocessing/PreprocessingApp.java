package com.opuscapita.peppol.preprocessing;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.preprocessing.controller.PreprocessingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.preprocessing"})
public class PreprocessingApp {
    @Value("${peppol.preprocessing.queue.in.name}")
    private String queueIn;
    @Value("${peppol.preprocessing.queue.out.name}")
    private String queueOut;

    public static void main(String[] args) {
        SpringApplication.run(PreprocessingApp.class, args);
    }

    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueIn);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    Gson gson() {
        return new Gson();
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull StatusReporter reporter, @NotNull Gson gson,
                                        @NotNull PreprocessingController controller, @NotNull RabbitTemplate rabbitTemplate) {
        return new AbstractQueueListener(errorHandler, reporter, gson) {
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                logger.debug("Message received " + cm.getFileName());
                cm = controller.process(cm);
                cm.setStatus("file read");
                rabbitTemplate.convertAndSend(queueOut, cm);
                logger.debug("Successfully processed and delivered to MQ");
            }
        };
    }

}
