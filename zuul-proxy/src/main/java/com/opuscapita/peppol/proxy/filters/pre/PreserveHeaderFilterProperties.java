package com.opuscapita.peppol.proxy.filters.pre;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bambr on 17.3.3.
 */
@ConfigurationProperties(prefix = "peppol.zuul.headers")
@Component
@RefreshScope
public class PreserveHeaderFilterProperties {
    List<String> headersToPreserve;

    public PreserveHeaderFilterProperties() {
    }

    public PreserveHeaderFilterProperties(String headersToPreserve) {
        setHeadersToPreserve(headersToPreserve);
    }

    public List<String> getHeadersToPreserve() {
        return headersToPreserve;
    }

    public void setHeadersToPreserve(String headersToPreserve) {
        this.headersToPreserve = Arrays.asList(headersToPreserve.split(","));
    }
}
