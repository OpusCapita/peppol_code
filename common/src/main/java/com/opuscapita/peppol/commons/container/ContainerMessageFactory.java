package com.opuscapita.peppol.commons.container;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.route.RoutingConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main entry point for container creation. Loads message from a given source and
 * sets all required fields.
 *
 * @author Sergejs.Roze
 */
@Component
@EnableConfigurationProperties(RoutingConfiguration.class)
public class ContainerMessageFactory {
    private final static Logger logger = LoggerFactory.getLogger(ContainerMessageFactory.class);

    private final DocumentLoader documentLoader;
    private final RoutingConfiguration routingConfiguration;
    private final ServiceNow serviceNow;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public ContainerMessageFactory(@NotNull DocumentLoader documentLoader, @NotNull RoutingConfiguration routingConfiguration, @NotNull ServiceNow serviceNow) {
        this.documentLoader = documentLoader;
        this.routingConfiguration = routingConfiguration;
        this.serviceNow = serviceNow;
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
    private Route loadRoute(@NotNull BaseDocument baseDocument, @NotNull Endpoint source) throws IOException {
        for (Route route : routingConfiguration.getRoutes()) {
            if (source == route.getSource()) {
                if (route.getMask() != null) {
                    if (baseDocument.getRecipientId().matches(route.getMask())) {
                        return route;
                    }
                } else {
                    return route;
                }
            }
        }
        openSncTicket(baseDocument, source);
        throw new IllegalArgumentException("Cannot define route for " + baseDocument.getFileName());
    }

    private void openSncTicket(BaseDocument baseDocument, Endpoint source) throws IOException {
        logger.error("No route found for document " + baseDocument.getFileName());
        SncEntity sncEntity = new SncEntity(
                "No route found for document",
                "Cannot find route for a document with id " + baseDocument.getFileName(),
                baseDocument.getDocumentId(),
                source == Endpoint.PEPPOL ? baseDocument.getRecipientId() : baseDocument.getSenderId(),
                3);

        serviceNow.insert(sncEntity);
    }
}

