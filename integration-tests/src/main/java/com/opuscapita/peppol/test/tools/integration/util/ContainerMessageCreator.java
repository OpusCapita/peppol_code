package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.opuscapita.peppol.commons.container.document.Archetype.*;

public class ContainerMessageCreator {
    private final static Logger logger = LoggerFactory.getLogger(ContainerMessageCreator.class);

    private Map<String, String> properties;
    private DocumentLoader documentLoader;

    public ContainerMessageCreator(DocumentLoader documentLoader, Map<String, String> properties) {
        this.documentLoader = documentLoader;
        this.properties = properties;
    }

    public ContainerMessage createContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(getSourceEndPoint(), getSourceEndPointType());
        ContainerMessage cm = new ContainerMessage(file.getAbsolutePath(), source);
        //loading document info
        if (loadFile()) {
            cm.setDocumentInfo(documentLoader.load(file, getLoaderEndpoint()));
        }
        //final endpoint current status
        if (!nullStatus()) //for SNC we need current status to be null to raise exception
            cm.setStatus(getCurrentEndpoint(), "delivered");
        //processing exception for
        if (properties.containsKey("processing exception")) {
            cm.getProcessingInfo().setProcessingException(properties.get("processing exception"));
        }

        if (!file.getName().contains("invalid")) {
            List<String> endpoints = Collections.singletonList(getRouteEndpoint()); //new queue for integration tests
            Route route = new Route();
            route.setEndpoints(endpoints);
            cm.getProcessingInfo().setRoute(route);
            cm.getProcessingInfo().setPeppolMessageMetadata(PeppolMessageMetadata.create(file.getName()));
        }

        if (properties.containsKey("archetype")) {
            cm.getDocumentInfo().setArchetype(getArchetype());
            logger.info("ContainerMessageCreator: archetype set to: " + cm.getDocumentInfo().getArchetype());
        }

        return cm;
    }

    /*check if document info needs to be loaded from file, default: true/yes*/
    private boolean loadFile() {
        return Boolean.valueOf(properties.getOrDefault("load file", "true"));
    }


    private boolean nullStatus() {
        return properties.containsKey("null status");
    }

    public ProcessType getProcessType() {
        String type = properties.get("endpoint type");
        if (type == null || type.isEmpty()) {
            return ProcessType.TEST;  //default
        }
        switch (type) {
            case "outbound":
                return ProcessType.OUT_OUTBOUND;
            case "outbound reprocess":
                return ProcessType.OUT_REPROCESS;
            case "test":
                return ProcessType.TEST;
            case "inbound":
                return ProcessType.IN_INBOUND;
            default:
                throw new IllegalArgumentException("ContainerMessageCreator: ProcessType not recognized: " + type);
        }
    }

    private String getSourceEndPoint() {
        return properties.getOrDefault("source endpoint", "integration-tests");
    }

    private String getRouteEndpoint() {
        return properties.getOrDefault("endpoint", "integration-test-endpoint");
    }

    private Endpoint getCurrentEndpoint() {
        return new Endpoint("integration-tests", getProcessType());
    }

    public Endpoint getLoaderEndpoint() {
        return new Endpoint("integration-tests", ProcessType.TEST);
    }

    private Archetype getArchetype() {
        String type = properties.get("archetype").toLowerCase();
        switch (type) {
            case "invalid":
                return INVALID;
            case "peppol bis":
                return PEPPOL_BIS;
            case "ehf":
                return EHF;
            case "svefaktura1":
                return SVEFAKTURA1;
            case "peppol si":
                return PEPPOL_SI;
            case "at":
                return AT;
            default:
                throw new IllegalArgumentException("unrecognized archetype: " + type);
        }
    }

    public ProcessType getSourceEndPointType() {
        String type = properties.getOrDefault("source type", "").toLowerCase();
        switch (type) {
            case "outbound reprocess":
                return ProcessType.OUT_REPROCESS;
            default:
                return ProcessType.TEST;
        }
    }
}
