package com.opuscapita.peppol.mlr;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import com.opuscapita.peppol.mlr.util.MlrController;
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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.mlr"})
@EnableJpaRepositories(basePackages = "com.opuscapita.peppol.mlr.util.model")
@EntityScan(basePackages = {"com.opuscapita.peppol.mlr.util.model", "com.opuscapita.peppol.commons.model"})
public class MlrReporterApplication {
    private static final Logger logger = LoggerFactory.getLogger(MlrReporterApplication.class);

    @Value("${peppol.mlr-reporter.queue.in.name}")
    private String queueIn;
    @Value("${peppol.component.name}")
    private String componentName;

    private final MlrController controller;
    private final ErrorHandler errorHandler;

    @Autowired
    public MlrReporterApplication(@NotNull MlrController controller, @NotNull ErrorHandler errorHandler) {
        this.controller = controller;
        this.errorHandler = errorHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(MlrReporterApplication.class, args);
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
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(this::consume);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @SuppressWarnings("ConstantConditions")
    private void consume(@NotNull ContainerMessage cm) {
        try {
            controller.process(cm);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Failed to create MLR: " + ex.getMessage(), ex);
            errorHandler.reportWithContainerMessage(cm, ex, "Exception during MLR creation");
        }
    }
}
