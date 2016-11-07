package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.route.Route;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "peppol.internal-routing.routing")
public class RoutingConfiguration {
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }

}
