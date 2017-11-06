package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.IntegrationTestApp;
import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.*;
import com.opuscapita.peppol.test.tools.integration.util.MqListener;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
@Component
public class SubscriberFactory {
    private final static Logger logger = LoggerFactory.getLogger(SubscriberFactory.class);


    public Subscriber createSubscriber(Map.Entry<String, ?> subscriberConfig, Map<String, Object> genericConfiguration, Map<String, Consumer> existingConsumers) {
        String name = subscriberConfig.getKey().toLowerCase();
        Map<String, Object> properties = (Map<String, Object>) subscriberConfig.getValue();
        String[] consumerIds = properties.get("consumers") == null? ArrayUtils.EMPTY_STRING_ARRAY : (String.valueOf(properties.get("consumers"))).split(" ");
        Subscriber subscriber = null;
        List<Consumer> consumers = new ArrayList<>();
        switch (name){
            case "mq subscriber":
                subscriber = new MqSubscriber(properties.get("timeout"), properties.get("source queue"));
                IntegrationTestApp.registerMqListener((MqListener) subscriber);
                break;
            case "snc subscriber":
                subscriber = new SncSubscriber(properties.get("timeout"), properties.get("source directory"));
                break;
            case "directory subscriber":
                subscriber = new DirectorySubscriber(properties.get("timeout"), properties.get("source directory"));
                break;
            case "db subscriber":
                String dbKey = (String) properties.get("db connection");
                String dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                subscriber = new DbSubscriber(properties.get("timeout"), dbConnection, properties.get("query"));
                break;
            case "file subscriber":
                subscriber = new FileSubscriber(properties.get("timeout"), properties.get("source file"));
                break;
            default:
                String errorText = "Invalid subscriber configuration, unable to create subscriber: " + name;
                logger.error(errorText);
                throw new IllegalStateException(errorText);
        }
        for (String consumerId : consumerIds) {
            if(existingConsumers.containsKey(consumerId))
                consumers.add(existingConsumers.get(consumerId));
        }
        subscriber.setConsumers(consumers);
        return subscriber;
    }
}
