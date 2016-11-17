package com.opuscapita.peppol.test.tools.integration.consumers;

import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.DbConsumer;
import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.QueueConsumer;
import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.SeleniumConsumer;

import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class ConsumerFactory {

    public static Consumer createConsumer(Map.Entry<String, ?> consumerConfig) {
        String name = consumerConfig.getKey();
        Map<String ,Object> properties = (Map<String, Object>) consumerConfig.getValue();

        switch (name){
            case "queue msg count check":
                return new QueueConsumer((List<String>)properties.get("subscribers"),properties.get("expected value"));
            case "DB query":
                return new DbConsumer(properties.get("expected value"));
            case "selenium check":
                return new SeleniumConsumer(properties.get("expected value"));
            default:
                throw new IllegalArgumentException("Invalid consumer configuration, unable to create consumer");
        }
    }
}
