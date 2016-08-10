package com.opuscapita.peppol.validator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.9.8.
 */
@ConfigurationProperties(prefix = "peppol.validation")
@Component
@RefreshScope
public class ValidatorProperties {
    private String consumeQueue;

    private String respondQueue;

    public String getConsumeQueue() {
        return consumeQueue;
    }

    public void setConsumeQueue(String consumeQueue) {
        this.consumeQueue = consumeQueue;
    }

    public String getRespondQueue() {
        return respondQueue;
    }

    public void setRespondQueue(String respondQueue) {
        this.respondQueue = respondQueue;
    }
}
