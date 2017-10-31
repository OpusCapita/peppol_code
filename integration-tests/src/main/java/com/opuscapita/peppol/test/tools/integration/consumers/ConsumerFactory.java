package com.opuscapita.peppol.test.tools.integration.consumers;

import com.opuscapita.peppol.test.tools.integration.consumers.subtypes.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.16..
 */
@Component
public class ConsumerFactory {

    public Consumer createConsumer(Map.Entry<String, ?> consumerConfig, Map<String, Object> genericConfiguration) {
        String name = consumerConfig.getKey().toLowerCase();
        Map<String, Object> properties = (Map<String, Object>) consumerConfig.getValue();
        String id = String.valueOf(properties.get("id"));
        Integer timeout = (Integer) properties.get("timeout");
        String testName = (String) properties.get("name");
        switch (name) {
            case "queue msg count check":
                return new MqConsumer(id, testName, (List<String>) properties.get("subscribers"), properties.get("expected value"));
            case "db check":
            case "db test":
                return new DbConsumer(id, testName, properties.get("expected value"));
            case "file download test":
                return new FileDownloadConsumer(id, properties.get("name"), properties.get("action"), properties.get("link"), properties.get("expected value"));
            case "snc test":
            case "snc check":
                String expected = (String) properties.get("expected value");
                return new SncConsumer(id, testName, expected, timeout);
            case "file test":
            case "file check":
                String expectedValue = (String) properties.get("expected value");
                return new FileConsumer(id, testName, expectedValue, timeout);
            case "mlr test":
            case "mlr check":
                String mlrExpectedValue = (String) properties.get("expected value");
                String expectedFile = (String) properties.get("expected file");
                return new MlrConsumer(id, testName, mlrExpectedValue, expectedFile, timeout);
            case "web ui check":
            case "web ui test":
                return new WebUiConsumer(id, testName, properties.get("expected value"));
            case "rest test":
            case "rest check":
                return new RestConsumer(id, testName, properties.get("expected value"));
            case "inbound message check":
            case "inbound message test":
                return new InboundFileConsumer(id, testName, String.valueOf(properties.get("expected value")), timeout);
            case "preprocessing check":
            case "preprocessing test":
                return new PreprocessingFileConsumer(id, testName, String.valueOf(properties.get("expected value")), timeout);
            default:
                throw new IllegalArgumentException("Invalid consumer configuration, unable to create consumer: " + testName);
        }
    }
}
