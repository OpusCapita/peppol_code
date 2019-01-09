package com.opuscapita.peppol.mlr;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.mlr.util.MlrController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.mlr"})
@EnableJpaRepositories(basePackages = "com.opuscapita.peppol.mlr.util.model")
@EntityScan(basePackages = {"com.opuscapita.peppol.mlr.util.model", "com.opuscapita.peppol.commons.model"})
public class MlrReporterApplication {

    @Value("${peppol.mlr-reporter.queue.in.name}")
    private String queueIn;
    @Value("${peppol.component.name}")
    private String componentName;

    private final MlrController mlrReporter;

    @Autowired
    public MlrReporterApplication(MlrController mlrReporter) {
        this.mlrReporter = mlrReporter;
    }

    public static void main(String[] args) {
        SpringApplication.run(MlrReporterApplication.class, args);
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
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(AbstractQueueListener queueListener) {
        return new MessageListenerAdapter(queueListener, "receiveMessage");
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull ContainerMessageSerializer serializer) {
        return new AbstractQueueListener(errorHandler, null, serializer) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) {
                if (cm == null || cm.getDocumentInfo() == null) {
                    logger.info("No document in received message, ignoring message");
                    return;
                }
                try {
                    mlrReporter.process(cm);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String shortDescription = "Exception during MLR processing for message: " + cm.toLog();
                    logger.error(shortDescription + " with exception: " + ex);
                    errorHandler.reportWithContainerMessage(cm, ex, shortDescription);
                }
            }
        };
    }
}
