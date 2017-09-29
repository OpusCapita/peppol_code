package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.opuscapita.peppol.commons.container.document.Archetype.INVALID;

public class ContainerMessageCreator {
    private final static Logger logger = LoggerFactory.getLogger(ContainerMessageCreator.class);

    private Map<String, String> properties;
    private DocumentLoader documentLoader;

    public ContainerMessageCreator(DocumentLoader documentLoader, Map<String, String> properties) {
        this.documentLoader = documentLoader;
        this.properties = properties;
    }

    public ContainerMessage createContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(getSourceEndPoint(), ProcessType.TEST);
        ContainerMessage cm = new ContainerMessage("integration-tests", file.getAbsolutePath(), source);
        //loading document info
        cm.setDocumentInfo(documentLoader.load(file, getLoaderEndpoint()));
        //final endpoint current status
        if (!nullStatus()) //for SNC we need current status to be null to raise exception
            cm.setStatus(getCurrentEndpoint(), "delivered");
        //processing exception for
        if (properties.containsKey("processing exception")) {
            cm.getProcessingInfo().setProcessingException(properties.get("processing exception"));
        }

        if(!file.getName().contains("invalid")) {
            List<String> endpoints = Collections.singletonList(getRouteEndpoint()); //new queue for integration tests
            cm.getProcessingInfo().setTransactionId("transactionId");
            Route route = new Route();
            route.setEndpoints(endpoints);
            cm.getProcessingInfo().setRoute(route);
        }

        if(properties.containsKey("archetype")){
            cm.getDocumentInfo().setArchetype(getArchetype());
            logger.info("ContainerMessageCreator: archetype set to: " + cm.getDocumentInfo().getArchetype());
        }

        return cm;
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
        switch (type){
            case "invalid" :
                return INVALID;
            default : throw new IllegalArgumentException("unrecognized archetype: " + type);
        }
    }

}
