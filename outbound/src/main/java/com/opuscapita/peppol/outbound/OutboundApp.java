package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.template.CommonMessageReceiver;
import com.opuscapita.peppol.outbound.controller.OutboundController;
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
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.outbound"})
@EnableDiscoveryClient
//@EnableScheduling
public class OutboundApp {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(OutboundApp.class);

    private final OutboundController controller;
    // private final DocumentLoader documentLoader;

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

    private boolean was = false;

    @Autowired
    public OutboundApp(@NotNull @Lazy OutboundController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(OutboundApp.class, args);
    }

//    @Scheduled(fixedDelay = 12_000)
//    public void postConstruct() {
//        if (was) {
//            ContainerMessage cm = new ContainerMessage();
//
//            ProcessingInfo pi = new ProcessingInfo(Endpoint.TEST, "test_endpoint");
//            cm.setProcessingInfo(pi);
//
//            cm.setFileName("/home/rozeser1/test/chernobyl.xml");
//
//            try {
//                DocumentInfo documentInfo = documentLoader.load("/home/rozeser1/test/chernobyl.xml", Endpoint.TEST);
//                cm.setDocumentInfo(documentInfo);
//
//                controller.send(cm);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            System.exit(0);
//        }
//        was = true;
//    }

    @Bean
    MessageListenerAdapter listenerAdapter(@NotNull CommonMessageReceiver receiver) {
        receiver.setContainerMessageConsumer(controller::send);
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @SuppressWarnings("Duplicates")
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setStopConsumerMinInterval(consumersTimeout);
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }


}
