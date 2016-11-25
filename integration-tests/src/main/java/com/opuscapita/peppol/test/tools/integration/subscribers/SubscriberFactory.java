package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.DbSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.FileSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.MqSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.SncSubscriber;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class SubscriberFactory {
    public static Subscriber createSubscriber(Map.Entry<String, ?> subscriberConfig, Map<String, Object> genericConfiguration) {
        String name = subscriberConfig.getKey().toLowerCase();
        Map<String, Object> properties = (Map<String, Object>) subscriberConfig.getValue();
        switch (name){
            case "mq subscriber":
                return new MqSubscriber(properties.get("timeout"));
            case "snc subscriber":
                return new SncSubscriber(properties.get("timeout"));
            case "db subscriber":
                return new DbSubscriber(properties.get("timeout"));
            case "file subscriber":
                return new FileSubscriber(properties.get("timeout"));
            default:
                throw new IllegalArgumentException("Invalid subscriber configuration, unable to create subscriber: " + name);
        }
    }
}
