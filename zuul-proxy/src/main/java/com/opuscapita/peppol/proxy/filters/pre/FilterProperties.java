package com.opuscapita.peppol.proxy.filters.pre;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by bambr on 16.28.12.
 */
@ConfigurationProperties(prefix = "peppol.zuul.proxy")
@Component
@RefreshScope
public class FilterProperties {
    private List<String> allowFrom;
    private List<String> denyFrom;
    private volatile Map<String, List<String>> servicesAllowFrom;
    private volatile Map<String, List<String>> servicesDenyFrom;

    public FilterProperties() {
    }

    public FilterProperties(String allowFrom, String denyFrom, Map<String, String> servicesAllowFrom, Map<String, String> servicesDenyFrom) {
        setAllowFrom(allowFrom);
        setDenyFrom(denyFrom);
        setServicesAllowFrom(servicesAllowFrom);
        setServicesDenyFrom(servicesDenyFrom);
    }

    public List<String> getAllowFrom() {
        return allowFrom;
    }

    public void setAllowFrom(String allowFrom) {
        this.allowFrom = allowFrom == null ? Collections.emptyList() : Arrays.asList(allowFrom.split(","));
    }

    public List<String> getDenyFrom() {
        return denyFrom;
    }

    public void setDenyFrom(String denyFrom) {
        this.denyFrom = denyFrom == null ? Collections.emptyList() : Arrays.asList(denyFrom.split(","));
    }

    public Map<String, List<String>> getServicesAllowFrom() {
        return servicesAllowFrom;
    }

    public void setServicesAllowFrom(Map<String, String> servicesAllowFrom) {
        if (servicesAllowFrom != null) {
            this.servicesAllowFrom = new HashMap<>(servicesAllowFrom.size());
            servicesAllowFrom.entrySet().forEach(entry -> this.servicesAllowFrom.put(entry.getKey(), Arrays.asList(entry.getValue().split(","))));
        } else {
            this.servicesAllowFrom = null;
        }
    }

    public Map<String, List<String>> getServicesDenyFrom() {
        return servicesDenyFrom;
    }

    public void setServicesDenyFrom(Map<String, String> servicesDenyFrom) {
        if (servicesDenyFrom != null) {
            this.servicesDenyFrom = new HashMap<>(servicesDenyFrom.size());
            servicesDenyFrom.entrySet().forEach(entry -> this.servicesDenyFrom.put(entry.getKey(), Arrays.asList(entry.getValue().split(","))));
        } else {
            this.servicesDenyFrom = null;
        }
    }
}
