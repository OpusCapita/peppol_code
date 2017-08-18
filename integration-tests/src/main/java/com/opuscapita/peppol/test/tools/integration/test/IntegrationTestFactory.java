package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.consumers.ConsumerFactory;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.producers.ProducerFactory;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.SubscriberFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gamanse1 on 2016.11.16..
 */
@Component
public class IntegrationTestFactory {
    private final static Logger logger = LoggerFactory.getLogger(IntegrationTestFactory.class);
    @Autowired private ProducerFactory producerFactory;
    @Autowired private SubscriberFactory subscriberFactory;
    @Autowired private ConsumerFactory consumerFactory;

    public IntegrationTest createTest(String moduleName, Map<String, ?> moduleSettings, Map<String, Object> genericConfiguration) {
        if (moduleName == null || moduleName.isEmpty()) {
            logger.error("module name not specified!");
            return null;
        }
        if(moduleSettings == null || moduleName.isEmpty()) {
            logger.error(moduleName + "config is empty !");
            return null;
        }
        List<Map<String,?>> producersConfiguration = (List<Map<String, ?>>) moduleSettings.get("producers");
        List<Map<String,?>> consumersConfiguration = (List<Map<String, ?>>) moduleSettings.get("consumers");
        List<Map<String,?>> subscribersConfiguration = (List<Map<String, ?>>) moduleSettings.get("subscribers");

        List<Producer> producers = createProducers(genericConfiguration, producersConfiguration);
        Map<String, Consumer> consumers = createConsumers(genericConfiguration, consumersConfiguration);
        List<Subscriber> subscribers = createSubscribers(genericConfiguration, subscribersConfiguration, consumers);


        return new IntegrationTest(moduleName, producers, subscribers, new ArrayList<>(consumers.values()));
    }

    private List<Subscriber> createSubscribers(Map<String, Object> genericConfiguration, List<Map<String, ?>> subscribersConfiguration, Map<String, Consumer> consumers) {
        return subscribersConfiguration.stream().map(subscriber -> subscriberFactory.createSubscriber(subscriber.entrySet().iterator().next(), genericConfiguration, consumers)).collect(Collectors.toList());
    }

    private List<Producer> createProducers(Map<String, Object> genericConfiguration, List<Map<String,?>> producersConfiguration) {
        List<Producer> producers = new ArrayList<>();
        for (Map<String, ?> producerConfig : producersConfiguration) {
            producers.add(producerFactory.createProducer(producerConfig.entrySet().iterator().next(), genericConfiguration));
        }
        return producers;
    }

    private Map<String, Consumer> createConsumers(Map<String, Object> genericConfiguration, List<Map<String,?>> consumersConfiguration){
        Map<String, Consumer> consumers = new HashMap<>();
        for (Map<String, ?> consumerConfig : consumersConfiguration) {
            Consumer consumer = consumerFactory.createConsumer(consumerConfig.entrySet().iterator().next(), genericConfiguration);
            consumers.put(consumer.getId(),consumer);
        }
        return consumers;
    }

}
