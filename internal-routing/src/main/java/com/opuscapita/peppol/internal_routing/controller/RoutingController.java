package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Sergejs.Roze
 */
@Component
public class RoutingController {
    private final static Logger logger = LoggerFactory.getLogger(RoutingController.class);

    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    private final RoutingConfiguration routingConfiguration;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RoutingController(@NotNull RoutingConfiguration routingConfiguration) {
        this.routingConfiguration = routingConfiguration;
    }

    @SuppressWarnings("ConstantConditions")
    public ContainerMessage loadRoute(ContainerMessage cm) throws IOException {
        logger.info("Processing message " + cm.getFileName());
        Endpoint source = cm.getSource();
        BaseDocument baseDocument = cm.getBaseDocument();

        if (baseDocument instanceof InvalidDocument) {
            Route error = new Route();
            error.setDescription("ERROR");
            error.setEndpoints(Collections.singletonList(errorQueue));
            error.setSource(cm.getSource());
            cm.setRoute(error);
            return cm;
        }

        for (Route route : routingConfiguration.getRoutes()) {
            if (source == route.getSource()) {
                if (route.getMask() != null) {
                    if (baseDocument.getRecipientId().matches(route.getMask())) {
                        logger.debug("Route selected by source and mask");
                        return cm.setRoute(new Route(route));
                    }
                } else {
                    logger.debug("Route selected by source");
                    return cm.setRoute(new Route(route));
                }
            }
        }
        throw new IllegalArgumentException("Cannot define route for " + cm.getFileName());
    }


}
