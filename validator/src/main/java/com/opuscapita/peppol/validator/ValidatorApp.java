package com.opuscapita.peppol.validator;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.context.annotation.Lazy;

/**
 * @author Sergejs.Roze
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.opuscapita.peppol")
@EnableCaching
public class ValidatorApp {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(ValidatorApp.class);

    @Value("${peppol.component.name}")
    String componentName;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    private final ValidationController controller;
    private final MessageQueue messageQueue;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ValidatorApp(@NotNull @Lazy ValidationController controller,
                        @NotNull @Lazy MessageQueue messageQueue) {
        this.controller = controller;
        this.messageQueue = messageQueue;
    }

    public static void main(String[] args) {
        SpringApplication.run(ValidatorApp.class, args);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(this::consume);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @SuppressWarnings("ConstantConditions")
    private void consume(@NotNull ContainerMessage cm) throws Exception {
        Endpoint endpoint = new Endpoint(componentName, cm.isInbound() ? ProcessType.IN_VALIDATION : ProcessType.OUT_VALIDATION);
        logger.info("Validating message " + cm.getFileName());

        cm.getProcessingInfo().setCurrentStatus(endpoint, "performing validation");
        EventingMessageUtil.reportEvent(cm, "Performing validation");
        cm = controller.validate(cm, endpoint);

        if (cm.hasErrors()) {
            cm.getProcessingInfo().setCurrentStatus(endpoint, "validation failed");
            EventingMessageUtil.reportEvent(cm, "Validation failed");
            messageQueue.convertAndSend(errorQueue, cm);
            logger.info("Validation failed for " + cm.getFileName() + ", message sent to " + errorQueue + " queue");
        } else {
            String queueOut = cm.popRoute();
            cm.setStatus(endpoint, "validation passed");
            EventingMessageUtil.reportEvent(cm, "Validation passed, sent to: " + queueOut);
            messageQueue.convertAndSend(queueOut, cm);
            logger.info("Validation passed for " + cm.getFileName() + ", message sent to " + queueOut + " queue");
        }
    }


}
