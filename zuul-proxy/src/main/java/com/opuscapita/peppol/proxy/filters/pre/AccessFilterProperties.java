package com.opuscapita.peppol.proxy.filters.pre;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bambr on 16.28.12.
 */
@ConfigurationProperties(prefix = "peppol.zuul.proxy")
@Component
@RefreshScope
public class AccessFilterProperties {
    private List<String> allowFrom;
    private List<String> denyFrom;
    private List<String> servicesToBypass = new ArrayList<String>() {{
        add("peppol-ap-inbound");
    }};
    private List<String> prohibitedMasks = new ArrayList<String>() {{
        add("admin");
    }};

    private List<String> prohibitedMasksNetworkOverrides = new ArrayList<>();
    private volatile Map<String, List<String>> servicesAllowFrom;
    private volatile Map<String, List<String>> servicesDenyFrom;

    public AccessFilterProperties() {
    }

    public AccessFilterProperties(String allowFrom, String denyFrom, String servicesToBypass, String prohibitedMasks, String prohibitedMasksNetworkOverride, Map<String, String> servicesAllowFrom, Map<String, String> servicesDenyFrom) {
        setAllowFrom(allowFrom);
        setDenyFrom(denyFrom);
        setProhibitedMasks(prohibitedMasks);
        setProhibitedMasksNetworkOverrides(prohibitedMasksNetworkOverride);
        setServicesToBypass(servicesToBypass);
        setServicesAllowFrom(servicesAllowFrom);
        setServicesDenyFrom(servicesDenyFrom);
    }

    public List<String> getServicesToBypass() {
        return servicesToBypass;
    }

    public void setServicesToBypass(String servicesToBypass) {
        if (servicesToBypass != null) {
            this.servicesToBypass = Arrays.asList(servicesToBypass.split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList());
        }
    }

    public List<String> getAllowFrom() {
        return allowFrom;
    }

    public void setAllowFrom(String allowFrom) {
        this.allowFrom = allowFrom == null ? Collections.emptyList() : Arrays.asList(allowFrom.split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList());
    }

    public List<String> getDenyFrom() {
        return denyFrom;
    }

    public void setDenyFrom(String denyFrom) {
        this.denyFrom = denyFrom == null ? Collections.emptyList() : Arrays.asList(denyFrom.split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList());
    }

    public Map<String, List<String>> getServicesAllowFrom() {
        return servicesAllowFrom;
    }

    public void setServicesAllowFrom(Map<String, String> servicesAllowFrom) {
        if (servicesAllowFrom != null) {
            this.servicesAllowFrom = new HashMap<>(servicesAllowFrom.size());
            servicesAllowFrom.entrySet().forEach(entry -> this.servicesAllowFrom.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList())));
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
            servicesDenyFrom.entrySet().forEach(entry -> this.servicesDenyFrom.put(entry.getKey(), Arrays.asList(entry.getValue().split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList())));
        } else {
            this.servicesDenyFrom = null;
        }
    }

    public List<String> getProhibitedMasks() {
        return prohibitedMasks;
    }

    public void setProhibitedMasks(String prohibitedMasks) {
        this.prohibitedMasks = prohibitedMasks == null ? Collections.emptyList() : Arrays.asList(prohibitedMasks.split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList());
    }

    public List<String> getProhibitedMasksNetworkOverrides() {
        return prohibitedMasksNetworkOverrides;
    }

    public void setProhibitedMasksNetworkOverrides(String prohibitedMasksNetworkOverride) {
        this.prohibitedMasksNetworkOverrides = prohibitedMasksNetworkOverride == null ? Collections.emptyList() : Arrays.asList(prohibitedMasksNetworkOverride.split(",")).stream().map(raw -> raw.trim()).collect(Collectors.toList());
    }
}
