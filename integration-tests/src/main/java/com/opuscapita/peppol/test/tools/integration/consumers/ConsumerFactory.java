package com.opuscapita.peppol.test.tools.integration.consumers;

import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.DbConsumer;
import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.QueueConsumer;
import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.SeleniumConsumer;
import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.SncConsumer;

import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class ConsumerFactory {

    public static Consumer createConsumer(Map.Entry<String, ?> consumerConfig, Map<String, String> genericConfiguration) {
        String name = consumerConfig.getKey().toLowerCase();
        Map<String ,Object> properties = (Map<String, Object>) consumerConfig.getValue();

        switch (name){
            case "queue msg count check":
                return new QueueConsumer((List<String>)properties.get("subscribers"),properties.get("expected value"));
            case "db check":
            case "db test":
                String connectionKey = (String) properties.get("connection string");
                String dbConnectionString = genericConfiguration.get(connectionKey);
                return new DbConsumer(dbConnectionString, properties.get("expected value"));
            case "selenium check":
                return new SeleniumConsumer(properties.get("expected value"));
            case "snc test":
            case "snc check":
                String testName = (String) properties.get("name");
                String expression = (String) properties.get("expression");
                boolean expected = (boolean) properties.get("expected value");
                return new SncConsumer(testName, expression, expected);
            default:
                throw new IllegalArgumentException("Invalid consumer configuration, unable to create consumer: " + name);
        }
    }
}
