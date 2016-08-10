package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.route.conf.RoutingConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Main entry point for container creation. Loads message from a given source and
 * sets all required fields.
 *
 * @author Sergejs.Roze
 */
@Component
public class ContainerMessageFactory {
    private final static Logger logger = LoggerFactory.getLogger(ContainerMessageFactory.class);

    private final DocumentLoader documentLoader;
    private final RoutingConfiguration routingConfiguration;

    @Autowired
    public ContainerMessageFactory(@NotNull DocumentLoader documentLoader, @NotNull RoutingConfiguration routingConfiguration) {
        this.documentLoader = documentLoader;
        this.routingConfiguration = routingConfiguration;
    }

    @NotNull
    public ContainerMessage create(@NotNull String fileName, @NotNull Endpoint source) throws IOException {
        return create(new File(fileName), source);
    }

    @NotNull
    public ContainerMessage create(@NotNull File file, @NotNull Endpoint source) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return create(is, file.getAbsolutePath(), source);
        }
    }

    @NotNull
    public ContainerMessage create(@NotNull InputStream inputStream, @NotNull String fileName, @NotNull Endpoint source) throws IOException {
        BaseDocument baseDocument = documentLoader.load(inputStream, fileName);
        Route route = loadRoute(baseDocument, source);
        return new ContainerMessage(route, baseDocument);
    }

    @SuppressWarnings("Convert2streamapi")
    @NotNull
    private Route loadRoute(@NotNull BaseDocument baseDocument, @NotNull Endpoint source) {
//        String postfix = DEFAULT_ROUTE;
//        String first = OUTBOUND;
//        if (source == Endpoint.PEPPOL) {
//            for (DestinationFilter destinationFilter : routingConfiguration.getFilters()) {
//                if (destinationFilter.matches(baseDocument.getRecipientId())) {
//                    postfix = destinationFilter.getDestination();
//                }
//            }
//            first = INBOUND + "_" + postfix;
//        }
//
//        for (String config : routingConfiguration.getRoutes()) {
//            config = config.trim();
//            if (config.startsWith(first)) {
//                return new Route(source, config);
//            }
//        }

        logger.error("No route found for document");
        return new Route(new ArrayList<>()); // TODO handle the error
    }
}

