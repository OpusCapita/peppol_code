package com.opuscapita.peppol.validator;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.validator.controller.ValidationController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;

/**
 * @author Sergejs.Roze
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.opuscapita.peppol")
@EnableCaching
public class ValidatorApp {
    private final static Logger logger = LoggerFactory.getLogger(ValidatorApp.class);

    @Value("${peppol.component.name}")
    String componentName;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    private final ValidationController controller;

    @Autowired
    public ValidatorApp(ValidationController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(ValidatorApp.class, args);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull AbstractQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    AbstractQueueListener queueListener(@NotNull ErrorHandler errorHandler, @Nullable StatusReporter statusReporter,
                                        @NotNull ContainerMessageSerializer serializer,
                                        @NotNull MessageQueue messageQueue) {
        return new AbstractQueueListener(errorHandler, statusReporter, serializer) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected void processMessage(@NotNull ContainerMessage cm) throws Exception {
                Endpoint endpoint = new Endpoint(componentName, cm.isInbound() ? ProcessType.IN_VALIDATION : ProcessType.OUT_VALIDATION);

                logger.info("Validating message " + cm.getFileName());
                cm.getProcessingInfo().setCurrentStatus(endpoint, "performing validation");
                cm = controller.validate(cm);

                if (cm.hasErrors()) {
                    cm.getProcessingInfo().setCurrentStatus(endpoint, "validation failed");
                    messageQueue.convertAndSend(errorQueue, cm);
                    logger.info("Validation failed for " + cm.getFileName() + ", message sent to " + errorQueue + " queue");
                } else {
                    String queueOut = cm.popRoute();
                    cm.setStatus(endpoint, "validation passed");
                    messageQueue.convertAndSend(queueOut, cm);
                    logger.info("Validation passed for " + cm.getFileName() + ", message sent to " + queueOut + " queue");
                }
            }
        };
    }

    @Bean
    public TransformerFactory transformerFactory() {
        return TransformerFactory.newInstance();
    }

    @Bean
    public SAXParserFactory saxParserFactory() {
        return SAXParserFactory.newInstance();
    }

    @Bean
    public XMLInputFactory xmlInputFactory() {
        return XMLInputFactory.newFactory();
    }

}
