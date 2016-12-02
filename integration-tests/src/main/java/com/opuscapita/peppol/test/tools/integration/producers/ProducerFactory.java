package com.opuscapita.peppol.test.tools.integration.producers;

import com.opuscapita.peppol.test.tools.integration.producers.subtypes.*;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class ProducerFactory {

    public static Producer createProducer(Map.Entry<String, ?> producerConfig, Map<String, Object> genericConfiguration) {
        String name = producerConfig.getKey().toLowerCase();
        Map<String,String> properties = (Map<String, String>) producerConfig.getValue();
        switch (name){
            case "file producer":
                 return new FileProducer(properties.get("source directory"),properties.get("destination directory"));
            case "mq producer":
                String mqKey = properties.get("mq connection");
                String dbKey = properties.get("db connection");
                String dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                String dbPreprocessQuery = (dbKey == null) ? null : properties.get("DB preprocess querry");
                return new MqProducer((Map<String, String>) genericConfiguration.get(mqKey), properties.get("source directory"),
                        properties.get("destination queue"), dbConnection, dbPreprocessQuery);
            case "rest producer":
                return new RestProducer(properties.get("source file"), properties.get("rest template file"),
                        properties.get("rest endpoint") ,properties.get("rest method"));
            case "db producer":
                return new DbProducer(properties.get("source query"));
            case "web ui producer":
                return new WebUiProducer(properties.get("source directory"), properties.get("destination link"), properties.get("result directory"));
            default:
                throw new IllegalArgumentException("invalid producer configuration, unable to create producer");

        }
    }
}
