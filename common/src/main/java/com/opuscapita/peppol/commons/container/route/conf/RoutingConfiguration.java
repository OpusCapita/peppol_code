package com.opuscapita.peppol.commons.container.route.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@ConfigurationProperties(prefix = "routing")
public class RoutingConfiguration {

    private List<String> routes = new ArrayList<>();

    public List<String> getRoutes() {
        return routes;
    }
}
