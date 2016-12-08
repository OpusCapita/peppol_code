package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.DbSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.FileSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.MqSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.SncSubscriber;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class SubscriberFactory {
    private final static Logger logger = LogManager.getLogger(SubscriberFactory.class);
    public static Subscriber createSubscriber(Map.Entry<String, ?> subscriberConfig, Map<String, Object> genericConfiguration, Map<String, Consumer> existingConsumers) {
        String name = subscriberConfig.getKey().toLowerCase();
        Map<String, Object> properties = (Map<String, Object>) subscriberConfig.getValue();
        String[] consumerIds = (String.valueOf(properties.get("consumers"))).split(" ");
        Subscriber subscriber = null;
        List<Consumer> consumers = new ArrayList<Consumer>();
        switch (name){
            case "mq subscriber":
                String mqKey = (String) properties.get("mq connection");
                subscriber = new MqSubscriber(properties.get("timeout"), (Map<String, String>) genericConfiguration.get(mqKey), properties.get("source-queue"));
                break;
            case "snc subscriber":
                subscriber = new SncSubscriber(properties.get("timeout"));
                break;
            case "db subscriber":
                subscriber = new DbSubscriber(properties.get("timeout"));
                break;
            case "file subscriber":
                subscriber = new FileSubscriber(properties.get("source directory"), properties.get("timeout"));
                break;
            default:
                logger.error("Invalid subscriber configuration, unable to create subscriber: " + name);
        }
        for (String consumerId : consumerIds) {
            consumers.add(existingConsumers.get(consumerId));
        }
        subscriber.setConsumers(consumers);
        return subscriber;
    }
}
