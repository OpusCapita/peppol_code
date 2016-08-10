package com.opuscapita.peppol.commons.container.route;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@ConfigurationProperties(prefix = "routing")
public class RoutingConfiguration {
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }

}
