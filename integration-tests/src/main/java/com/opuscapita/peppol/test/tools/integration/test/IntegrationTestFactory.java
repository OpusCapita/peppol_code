package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.consumers.ConsumerFactory;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.producers.ProducerFactory;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.SubscriberFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class IntegrationTestFactory {
    private final static Logger logger = LogManager.getLogger(IntegrationTestFactory.class);

    public static IntegrationTest createTest(String moduleName, Map<String, ?> moduleSettings, Map<String, String> genericConfiguration) {
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
        List<Subscriber> subscribers = subscribersConfiguration.entrySet().stream().map(entry -> SubscriberFactory.createSubscriber(entry, genericConfiguration)).collect(Collectors.toList());
        List<Consumer> consumers = consumersConfiguration.entrySet().stream().map(entry -> ConsumerFactory.createConsumer(entry, genericConfiguration)).collect(Collectors.toList());

        return new IntegrationTest(moduleName, producers, subscribers, consumers);
    }
}
