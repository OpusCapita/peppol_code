package com.opuscapita.peppol.validator;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ValidatorApp(@NotNull @Lazy ValidationController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(ValidatorApp.class, args);
    }

    // TODO remove performance tests when finished with testing @SR
//    @PostConstruct
//    public void postConstruct() throws Exception {
//        String file = "/home/redis/work/validator/test-peppol/validator/src/test/resources/test_data/difi_files/Valids2-list-ejt.xml";
//        //String file = "/home/rozeser1/test-peppol/validator/src/test/resources/test_data/difi_files/Valids2-list-ejt.xml";
//        ContainerMessage cm = new ContainerMessage("meatdata", file, Endpoint.TEST);
//        DocumentInfo di = new DocumentInfo();
//        di.setCustomizationId("urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0");
//        di.setProfileId("urn:www.cenbii.eu:profile:bii05:ver2.0");
//        di.setRootNodeName("CreditNote");
//        cm.setDocumentInfo(di);
//
//        for (int i = 0; i < 1000000; i++) {
//            //System.out.println(i);
//            controller.validate(cm);
//        }
//        System.out.println("");
//        for (DocumentError e : cm.getDocumentInfo().getErrors()) {
//            System.out.println(e);
//        }
//
//        System.exit(0);
//    }


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
                EventingMessageUtil.reportEvent(cm, "Performing validation");
                cm = controller.validate(cm);

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
        };
    }

}
