package com.opuscapita.peppol.test.tools.integration.producers;

import com.opuscapita.peppol.test.tools.integration.producers.subtypes.DbProducer;
import com.opuscapita.peppol.test.tools.integration.producers.subtypes.FileProducer;
import com.opuscapita.peppol.test.tools.integration.producers.subtypes.MqProducer;
import com.opuscapita.peppol.test.tools.integration.producers.subtypes.RestProducer;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class ProducerFactory {

    public static Producer createProducer(Map.Entry<String, ?> producerConfig, Map<String, String> genericConfiguration) {
        String name = producerConfig.getKey();
        Map<String,String> properties = (Map<String, String>) producerConfig.getValue();
        switch (name){
            case "file producer":
                 return new FileProducer(properties.get("source directory"),properties.get("destination directory"));
            case "mq producer":
                return new MqProducer(properties.get("source directory"), properties.get("destination queue"));
            case "rest producer":
                return new RestProducer(properties.get("source file"), properties.get("rest template file"),
                        properties.get("rest endpoint") ,properties.get("rest method"));
            case "db producer":
                return new DbProducer(properties.get("source query"));
            default:
                throw new IllegalArgumentException("invalid producer configuration, unable to create producer");

        }
    }
}
