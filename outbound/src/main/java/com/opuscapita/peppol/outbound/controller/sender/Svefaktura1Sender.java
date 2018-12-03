package com.opuscapita.peppol.outbound.controller.sender;

import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import no.difi.vefa.peppol.common.model.Scheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by bambr on 16.21.11.
 */
@Component
@Scope("prototype")
public class Svefaktura1Sender extends UblSender {

    @Autowired
    public Svefaktura1Sender(OxalisWrapper oxalisWrapper) {
        super(oxalisWrapper);
    }

    @PostConstruct
    @Autowired
    @Override
    public void initialize() {
        oxalisOutboundModule = oxalisWrapper.getOxalisOutboundModule();
    }

    @Override
    protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisWrapper.getTransmissionRequestBuilder(true);
    }

    @Override
    protected Scheme getProcessIdentifierScheme() {
        return Scheme.of("sfti-procid");
    }
}
