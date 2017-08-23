package com.opuscapita.peppol.test.tools.integration.producers;

import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.test.tools.integration.producers.subtypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
@Component
public class ProducerFactory {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private MessageQueue mq;

    public Producer createProducer(Map.Entry<String, ?> producerConfig, Map<String, Object> genericConfiguration) {
        String name = producerConfig.getKey().toLowerCase();
        Map<String,String> properties = (Map<String, String>) producerConfig.getValue();
        String dbKey;
        String dbConnection;
        switch (name){
            case "file producer":
                 return new FileProducer(properties.get("source directory"),properties.get("destination directory"));
            case "mq producer":
                String mqKey = properties.get("mq connection");
                dbKey = properties.get("db connection");
                String sourceEndpoint = properties.get("source");
                String processType = properties.get("endpoint type");
                dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                String dbPreprocessQuery = (dbKey == null) ? null : properties.get("DB preprocess querry");
                MqProducer mqProducer = new MqProducer((Map<String, String>) genericConfiguration.get(mqKey), properties.get("source directory"),
                        properties.get("destination queue"), properties.get("endpoint"), dbConnection, dbPreprocessQuery, mq);
                if(processType != null && !processType.isEmpty())
                    mqProducer.setProcessType(processType);
                if(sourceEndpoint != null && !sourceEndpoint.isEmpty())
                    mqProducer.setSourceEndpoint(sourceEndpoint);
                beanFactory.autowireBean(mqProducer);
                return mqProducer;
            case "rest producer":
                String restResultDirectory = (String) genericConfiguration.get("validation result folder");
                return new RestProducer(properties.get("source directory"), properties.get("destination link"),properties.get("rest method"), restResultDirectory);
            case "db producer":
                dbKey = properties.get("db connection");
                dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                return new DbProducer(dbConnection, properties.get("source query"));
            case "web ui producer":
                String resultDirectory = (String) genericConfiguration.get("validation result folder");
                return new WebUiProducer(properties.get("source directory"), properties.get("destination link"), resultDirectory);
            case "command producer":
                return new CommandProducer(properties.get("command"));
            default:
                throw new IllegalArgumentException("invalid producer configuration, unable to create producer");

        }
    }
}
