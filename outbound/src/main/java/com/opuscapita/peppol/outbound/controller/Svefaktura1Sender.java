package com.opuscapita.peppol.outbound.controller;

import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.OxalisOutboundModuleWrapper;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by bambr on 16.21.11.
 */
@Component
public class Svefaktura1Sender extends UblSender {
    @Autowired
    OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper;
    private TransmissionRequestBuilder requestBuilder;
    private OxalisOutboundModule oxalisOutboundModule;

    @PostConstruct
    @Autowired
    public void initialize() {
        oxalisOutboundModule = oxalisOutboundModuleWrapper.getOxalisOutboundModule();
        requestBuilder = oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true);
    }
}