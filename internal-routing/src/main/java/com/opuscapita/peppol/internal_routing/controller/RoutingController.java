package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class RoutingController {
    private final static Logger logger = LoggerFactory.getLogger(RoutingController.class);

    private final RoutingConfiguration routingConfiguration;
    private final ErrorHandler errorHandler;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RoutingController(@NotNull RoutingConfiguration routingConfiguration, @Nullable ErrorHandler errorHandler) {
        this.routingConfiguration = routingConfiguration;
        this.errorHandler = errorHandler;
    }

    public ContainerMessage loadRoute(ContainerMessage cm) throws IOException {
        Endpoint source = cm.getSource();
        BaseDocument baseDocument = cm.getBaseDocument();
        for (Route route : routingConfiguration.getRoutes()) {
            if (source == route.getSource()) {
                if (route.getMask() != null) {
                    if (baseDocument.getRecipientId().matches(route.getMask())) {
                        return cm.setRoute(route);
                    }
                } else {
                    return cm.setRoute(route);
                }
            }
        }
        openSncTicket(baseDocument, source);
        throw new IllegalArgumentException("Cannot define route for " + baseDocument.getFileName());
    }

    private void openSncTicket(BaseDocument baseDocument, Endpoint source) throws IOException {
        logger.error("No route found for document " + baseDocument.getFileName());
        if (errorHandler == null) {
            return;
        }
        errorHandler.reportToServiceNow(
                "Cannot find route for a document with id " + baseDocument.getFileName(),
                source == Endpoint.PEPPOL ? baseDocument.getRecipientId() : baseDocument.getSenderId(),
                new IllegalArgumentException("Unable to create ContainerMessage"), "Can't find route"
        );
    }

}
