package com.opuscapita.peppol.outbound.controller.sender;

import eu.peppol.outbound.transmission.OxalisOutboundModuleWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by bambr on 16.21.11.
 */
@Component
public class Svefaktura1Sender extends UblSender {
    @Autowired
    public Svefaktura1Sender(OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper) {
        super(oxalisOutboundModuleWrapper);
    }

    @PostConstruct
    @Autowired
    @Override
    public void initialize() {
        oxalisOutboundModule = oxalisOutboundModuleWrapper.getOxalisOutboundModule();
        requestBuilder = oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true);
    }
}
