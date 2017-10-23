package com.opuscapita.peppol.wwd;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.wwd.webwatchdog.WebWatchDogReporter;
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
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.wwd.webwatchdog"})
public class WebWatchDogApplication {

    @Value("${peppol.wwd-reporter.queue.in.name}")
    private String queueIn;
    @Value("${peppol.component.name}")
    private String componentName;
    @Autowired
    private WebWatchDogReporter webWatchDogReporter;

    public static void main(String[] args) {
        SpringApplication.run(WebWatchDogReporter.class, args);
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
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                if (cm == null || cm.getDocumentInfo() == null) {
                    logger.info("No document in received message, ignoring message");
                    return;
                }
                try {
                    webWatchDogReporter.process(cm);
                }
                catch (Exception ex){
                    logger.error("WebWatchdogReporter failed wit exception: " + ex.getMessage());
                    errorHandler.reportWithContainerMessage(cm, ex, "Exception during reporting to Web Watch Dog");
                }
            }
        };
    }
}
