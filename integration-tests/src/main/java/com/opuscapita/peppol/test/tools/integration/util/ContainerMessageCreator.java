package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContainerMessageCreator {
    private Map<String, String> properties;
    private DocumentLoader documentLoader;

    public ContainerMessageCreator(DocumentLoader documentLoader, Map<String, String> properties) {
        this.documentLoader = documentLoader;
        this.properties = properties;
    }

    public ContainerMessage createContainerMessage(File file) throws Exception {
        return file.getName().contains("invalid") ? createInvalidContainerMessage(file) : createValidContainerMessage(file);
    }

    private ContainerMessage createValidContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(getSourceEndPoint(), ProcessType.TEST);
        ContainerMessage cm = new ContainerMessage("integration-tests", file.getAbsolutePath(), source)
                .setDocumentInfo(documentLoader.load(file, getLoaderEndpoint()));
        //final endpoint current status
        cm.setStatus(getCurrentEndpoint(), "delivered");
        List<String> endpoints = Collections.singletonList(getRouteEndpoint()); //new queue for integration tests
        cm.getProcessingInfo().setTransactionId("transactionId");
        Route route = new Route();
        route.setEndpoints(endpoints);
        cm.getProcessingInfo().setRoute(route);
        return cm;
    }


    private ContainerMessage createInvalidContainerMessage(File file) throws Exception {
        Endpoint source = new Endpoint(getSourceEndPoint(), ProcessType.TEST);
        ContainerMessage cm = new ContainerMessage("integration-tests", file.getAbsolutePath(), source)
                .setDocumentInfo(documentLoader.load(file, getLoaderEndpoint()));
        cm.getProcessingInfo().setProcessingException("This sending expected to fail I/O in test mode");
        if (!nullStatus()) //for SNC we need current status to be null to raise exception
            cm.setStatus(getCurrentEndpoint(), "delivered");
        return cm;
    }

    private boolean nullStatus() {
        return properties.containsKey("null status");
    }

    public ProcessType getProcessType() {
        String type = properties.get("process type");
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
}
