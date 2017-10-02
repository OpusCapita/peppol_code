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
        Map<String,Object> properties = (Map<String, Object>) producerConfig.getValue();
        String dbKey;
        String dbConnection;
        switch (name){
            case "file producer":
                 return new FileProducer(properties.get("source directory"),properties.get("destination directory"));
            case "mq producer":
                String mqKey = (String) properties.get("mq connection");
                dbKey = (String) properties.get("db connection");
                dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                String dbPreprocessQuery = (dbKey == null) ? null : (String) properties.get("DB preprocess querry");
                MqProducer mqProducer = new MqProducer((Map<String, String>) genericConfiguration.get(mqKey),
                        (String) properties.get("destination queue"),
                        (String) properties.get("source directory"),
                        (Map<String, String>) properties.get("container message"),
                        mq, dbConnection, dbPreprocessQuery);
                beanFactory.autowireBean(mqProducer);
                return mqProducer;
            case "rest producer":
                String restResultDirectory = (String) genericConfiguration.get("validation result folder");
                return new RestProducer(properties.get("source directory"), properties.get("destination link"),properties.get("rest method"), restResultDirectory);
            case "db producer":
                dbKey = (String) properties.get("db connection");
                dbConnection = (dbKey == null) ? null : (String) genericConfiguration.get(dbKey);
                return new DbProducer(dbConnection, properties.get("source query"));
            case "web ui producer":
                String resultDirectory = (String) genericConfiguration.get("validation result folder");
                return new WebUiProducer((String) properties.get("source directory"), (String) properties.get("destination link"), resultDirectory);
            case "command producer":
                return new CommandProducer((String) properties.get("command"), (String) properties.get("preprocessing cleanup dir"));
            default:
                throw new IllegalArgumentException("invalid producer configuration, unable to create producer");

        }
    }
}
