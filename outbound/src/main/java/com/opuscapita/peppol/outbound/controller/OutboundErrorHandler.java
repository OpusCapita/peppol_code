package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundErrorHandler {

    @Value("${peppol.component.name}")
    private String componentName;

    void handleError(ContainerMessage cm, Exception e) throws Exception {
        cm.setStatus(new Endpoint(componentName, ProcessType.OUT_OUTBOUND), "failed to deliver");
        throw new Exception(e);
    }

}
