package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.IntegrationTestApp;
import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.DbSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.FileSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.MqSubscriber;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.SncSubscriber;
import com.opuscapita.peppol.test.tools.integration.util.MqListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
@Component
public class SubscriberFactory {
    private final static Logger logger = LogManager.getLogger(SubscriberFactory.class);


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
            case "db subscriber":
                String dbKey = (String) properties.get("db connection");
                String dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                subscriber = new DbSubscriber(properties.get("timeout"), dbConnection, properties.get("query"));
                break;
            case "file subscriber":
                subscriber = new FileSubscriber(properties.get("timeout"), properties.get("source file"));
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
