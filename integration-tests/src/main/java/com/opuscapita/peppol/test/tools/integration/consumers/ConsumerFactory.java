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
        Map<String ,Object> properties = (Map<String, Object>) consumerConfig.getValue();
        String id = String.valueOf(properties.get("id"));
        Integer timeout = (properties.get("endpoint type") == null ) ? null :  (Integer)properties.get("endpoint type");
        switch (name){
            case "queue msg count check":
                return new MqConsumer(id, (String) properties.get("name"), (List<String>)properties.get("subscribers"),properties.get("expected value"));
            case "db check":
            case "db test":
                String consumerName = (String) properties.get("name");
                return new DbConsumer(id, consumerName, properties.get("expected value"));
            case "file download test":
                return new FileDownloadConsumer(id, properties.get("name"), properties.get("action"), properties.get("link"), properties.get("expected value"));
            case "snc test":
            case "snc check":
                String sncTestName = (String) properties.get("name");
                String expected = (String) properties.get("expected value");
                return new SncConsumer(id, sncTestName, expected, timeout);
            case "file test":
            case "file check":
                String fileTestName = (String) properties.get("name");
                String expectedValue = (String) properties.get("expected value");
                return new FileConsumer(id, fileTestName, expectedValue, timeout);
            case "mlr test":
            case "mlr check":
                String mlrFileTestName = (String) properties.get("name");
                String mlrExpectedValue = (String) properties.get("expected value");
                return new MlrConsumer(id, mlrFileTestName, mlrExpectedValue, timeout);
            case "wwd test":
                String wwdFileTestName = (String) properties.get("name");
                String wwdExpectedValue = (String) properties.get("expected value");
                return new WebWatchDogConsumer(id, wwdFileTestName, wwdExpectedValue, timeout);
            case "web ui check":
            case "web ui test":
                return new WebUiConsumer(id, (String) properties.get("name"), properties.get("expected value"));
            case "rest test":
            case "rest check":
                return new RestConsumer(id, (String)properties.get("name"), properties.get("expected value"));
            default:
                throw new IllegalArgumentException("Invalid consumer configuration, unable to create consumer: " + name);
        }
    }
}
