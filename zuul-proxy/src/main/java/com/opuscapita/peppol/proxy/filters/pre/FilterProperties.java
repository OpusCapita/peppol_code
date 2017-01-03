package com.opuscapita.peppol.proxy.filters.pre;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by bambr on 16.28.12.
 */
@ConfigurationProperties(prefix = "peppol.zuul.proxy")
@Component
@RefreshScope
public class FilterProperties {
    private String allowFrom;
    private String denyFrom;
    private Map<String, String> servicesAllowFrom;
    private Map<String, String> servicesDenyFrom;

    public FilterProperties() {
    }

    public FilterProperties(String allowFrom, String denyFrom, Map<String, String> servicesAllowFrom, Map<String, String> servicesDenyFrom) {
        this.allowFrom = allowFrom;
        this.denyFrom = denyFrom;
        this.servicesAllowFrom = servicesAllowFrom;
        this.servicesDenyFrom = servicesDenyFrom;
    }

    public String getAllowFrom() {
        return allowFrom;
    }

    public void setAllowFrom(String allowFrom) {
        this.allowFrom = allowFrom;
    }

    public String getDenyFrom() {
        return denyFrom;
    }

    public void setDenyFrom(String denyFrom) {
        this.denyFrom = denyFrom;
    }

    public Map<String, String> getServicesAllowFrom() {
        return servicesAllowFrom;
    }

    public void setServicesAllowFrom(Map<String, String> servicesAllowFrom) {
        this.servicesAllowFrom = servicesAllowFrom;
    }

    public Map<String, String> getServicesDenyFrom() {
        return servicesDenyFrom;
    }

    public void setServicesDenyFrom(Map<String, String> servicesDenyFrom) {
        this.servicesDenyFrom = servicesDenyFrom;
    }
}
