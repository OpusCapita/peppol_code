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
    private static Map<String, Consumer> consumers = new HashMap<>();

    public static IntegrationTest createTest(String moduleName, Map<String, ?> moduleSettings, Map<String, Object> genericConfiguration) {
        if (moduleName == null || moduleName.isEmpty()) {
            logger.error("module name not specified!");
            return null;
        }
        if(moduleSettings == null || moduleName.isEmpty()) {
            logger.error(moduleName + "config is empty !");
            return null;
        }
        Map<String,?> producersConfiguration = (Map<String, ?>) moduleSettings.get("producers");
        Map<String,?> subscribersConfiguration = (Map<String, ?>) moduleSettings.get("subscribers");
        Map<String,?> consumersConfiguration = (Map<String, ?>) moduleSettings.get("consumers");

        List<Producer> producers = producersConfiguration.entrySet().stream().map(entry -> ProducerFactory.createProducer(entry, genericConfiguration)).collect(Collectors.toList());
        for (Map.Entry<String, ?> consumerConfig : consumersConfiguration.entrySet()) {
            Consumer consumer = ConsumerFactory.createConsumer(consumerConfig, genericConfiguration);
            consumers.put(consumer.getId(),consumer);
        }
        List<Subscriber> subscribers = subscribersConfiguration.entrySet().stream().map(entry -> SubscriberFactory.createSubscriber(entry, genericConfiguration, consumers)).collect(Collectors.toList());


        return new IntegrationTest(moduleName, producers, subscribers, new ArrayList<>(consumers.values()));
    }
}
