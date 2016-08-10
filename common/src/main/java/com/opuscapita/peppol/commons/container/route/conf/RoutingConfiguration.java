package com.opuscapita.peppol.commons.container.route.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "routing")
public class RoutingConfiguration {

    private List<String> routes = new ArrayList<>();
    private List<DestinationFilter> filters = new ArrayList<>();

    public List<String> getRoutes() {
        return routes;
    }

    public List<DestinationFilter> getFilters() {
        return filters;
    }
}
