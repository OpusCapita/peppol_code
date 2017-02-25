package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
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
    private final ErrorHandler errorHandler;
    private final StatusReporter statusReporter;
    @Value("${peppol.email-notificator.queue.in.name}")
    private String errorQueue;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RoutingController(@NotNull RoutingConfiguration routingConfiguration, @NotNull ErrorHandler errorHandler,
                             @NotNull StatusReporter statusReporter) {
        this.routingConfiguration = routingConfiguration;
        this.errorHandler = errorHandler;
        this.statusReporter = statusReporter;
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
        logger.info("Processing message " + cm.getFileName());
        String source = cm.getSource().getName();
        BaseDocument baseDocument = cm.getBaseDocument();

        if (baseDocument instanceof InvalidDocument) {
            Route error = new Route();
            error.setDescription("ERROR");
            error.setEndpoints(Collections.singletonList(errorQueue));
            error.setSource(cm.getSource().getName());
            cm.setRoute(error);
            return cm;
        }

        for (Route route : routingConfiguration.getRoutes()) {
            if (Objects.equals(source, route.getSource())) {
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
        logger.warn("Cannot define route for " + cm.getFileName() + " originated from " + cm.getSource());
        return cm.setRoute(null);
    }


}
