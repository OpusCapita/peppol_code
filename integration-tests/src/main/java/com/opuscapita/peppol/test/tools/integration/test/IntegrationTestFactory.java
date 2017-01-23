package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.consumers.ConsumerFactory;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.producers.ProducerFactory;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.SubscriberFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class IntegrationTestFactory {
    private final static Logger logger = LogManager.getLogger(IntegrationTestFactory.class);

    public static IntegrationTest createTest(String moduleName, Map<String, ?> moduleSettings, Map<String, Object> genericConfiguration) {
        if (moduleName == null || moduleName.isEmpty()) {
            logger.error("module name not specified!");
            return null;
        }
        if(moduleSettings == null || moduleName.isEmpty()) {
            logger.error(moduleName + "config is empty !");
            return null;
        }
        //TODO refactor
        Map<String,?> producersConfiguration = (Map<String, ?>) moduleSettings.get("producers");
        List<Map<String,?>> consumersConfiguration = (List<Map<String, ?>>) moduleSettings.get("consumers");
        List<Map<String,?>> subscribersConfiguration = (List<Map<String, ?>>) moduleSettings.get("subscribers");

        List<Producer> producers = createProducers(genericConfiguration, producersConfiguration);
        Map<String, Consumer> consumers = createConsumers(genericConfiguration, consumersConfiguration);
        List<Subscriber> subscribers = createSubscribers(genericConfiguration, subscribersConfiguration, consumers);


        return new IntegrationTest(moduleName, producers, subscribers, new ArrayList<>(consumers.values()));
    }

    private static List<Subscriber> createSubscribers(Map<String, Object> genericConfiguration, List<Map<String, ?>> subscribersConfiguration, Map<String, Consumer> consumers) {
        return subscribersConfiguration.stream().map(subscriber -> SubscriberFactory.createSubscriber(subscriber.entrySet().iterator().next(), genericConfiguration, consumers)).collect(Collectors.toList());
    }

    private static List<Producer> createProducers(Map<String, Object> genericConfiguration, Map<String, ?> producersConfiguration) {
        return producersConfiguration.entrySet().stream().map(entry -> ProducerFactory.createProducer(entry, genericConfiguration)).collect(Collectors.toList());
    }

    private static Map<String, Consumer> createConsumers(Map<String, Object> genericConfiguration, List<Map<String,?>> consumersConfiguration){
         Map<String, Consumer> consumers = new HashMap<>();
        for (Map<String, ?> consumerConfig : consumersConfiguration) {
            Consumer consumer = ConsumerFactory.createConsumer(consumerConfig.entrySet().iterator().next(), genericConfiguration);
            consumers.put(consumer.getId(),consumer);
        }
        return consumers;
    }

}
