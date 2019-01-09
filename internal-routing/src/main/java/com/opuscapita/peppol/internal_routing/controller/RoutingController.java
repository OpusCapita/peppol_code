package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Route;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Sergejs.Roze
 */
@Component
public class RoutingController {
    private final static Logger logger = LoggerFactory.getLogger(RoutingController.class);
    private final RoutingConfiguration routingConfiguration;

    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RoutingController(@NotNull RoutingConfiguration routingConfiguration) {
        this.routingConfiguration = routingConfiguration;
    }

    /**
     * Loads route for the message depending on the configuration and return the same message with the route set.
     * In case the route cannot be defined returns the original message without the route.
     *
     * @param cm the container message
     * @return the same message with route set if this route can be defined, otherwise the same message without any route
     * @throws IOException life is hard
     */
    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage loadRoute(@NotNull ContainerMessage cm) throws IOException {
        logger.info("Processing message: " + cm.toLog());
        String source = cm.getProcessingInfo().getSource().getName();
        DocumentInfo documentInfo = cm.getDocumentInfo();

        if (documentInfo.getArchetype() == Archetype.INVALID || documentInfo.getArchetype() == Archetype.UNRECOGNIZED) {
            Route errorRoute = new Route();
            errorRoute.setDescription("ERROR");
            errorRoute.setEndpoints(Collections.singletonList(errorQueue));
            errorRoute.setSource(cm.getProcessingInfo().getSource().getName());
            cm.getProcessingInfo().setRoute(errorRoute);
            return cm;
        }

        for (Route route : routingConfiguration.getRoutes()) {
            if (Objects.equals(source, route.getSource())) {
                if (route.getMask() != null) {
                    if (documentInfo.getRecipientId().matches(route.getMask())) {
                        logger.debug("Route selected by source and mask");
                        cm.getProcessingInfo().setRoute(new Route(route));
                        return cm;
                    }
                } else {
                    logger.debug("Route selected by source");
                    cm.getProcessingInfo().setRoute(new Route(route));
                    return cm;
                }
            }
        }
        logger.warn("Cannot define route for " + cm.toLog() + " originated from " + cm.getProcessingInfo().getSource());
        cm.getProcessingInfo().setRoute(null);
        return cm;
    }


}
