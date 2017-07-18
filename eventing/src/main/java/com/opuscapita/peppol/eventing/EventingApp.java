package com.opuscapita.peppol.eventing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.eventing.destinations.ReportingManager;
import com.opuscapita.peppol.eventing.destinations.webwatchdog.WebWatchDogConfig;
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
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * Gets messages from different modules and produces messages for events-persistence.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.eventing", "com.opuscapita.peppol.eventing.destinations.webwatchdog"})
@EnableDiscoveryClient
public class EventingApp {

    @Autowired
    private ReportingManager reportingManager;

    @Value("${peppol.eventing.queue.in.name}")
    private String queueIn;
    @Value("${peppol.component.name}")
    private String componentName;

    public static void main(String[] args) {
        SpringApplication.run(EventingApp.class, args);
    }

    @Bean
    WebWatchDogConfig webWatchDogConfig(@Value("${wwd.folder}") String webWatchDogFolder, @Value("${wwd.prefix}") String webWatchDogPrefix) {
        return new WebWatchDogConfig(webWatchDogFolder, webWatchDogPrefix);
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
    public Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    @Bean
    AbstractQueueListener queueListener(@Nullable ErrorHandler errorHandler, @NotNull Gson gson) {
        return new AbstractQueueListener(errorHandler, null, gson) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                if (cm == null || cm.getDocumentInfo() == null) {
                    logger.warn("No document in received message, ignoring message");
                    return;
                }
                reportingManager.report(cm);
            }
        };
    }

}
