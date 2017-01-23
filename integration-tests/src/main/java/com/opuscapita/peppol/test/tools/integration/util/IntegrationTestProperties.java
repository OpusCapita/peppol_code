package com.opuscapita.peppol.test.tools.integration.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.28.12.
 */
@ConfigurationProperties(prefix = "integration.test")
@Component
@RefreshScope
public class IntegrationTestProperties {
    private String queues;

    public IntegrationTestProperties() {
    }

    public IntegrationTestProperties(String queues) {
        this.queues = queues;
    }

    public String getQueues() {
        return queues;
    }

    public void setQueues(String queues) {
        this.queues = queues;
    }
}
