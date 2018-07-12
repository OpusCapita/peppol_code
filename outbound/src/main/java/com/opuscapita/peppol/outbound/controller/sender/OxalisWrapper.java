package com.opuscapita.peppol.outbound.controller.sender;

import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.21.11.
 */
@Component
public class OxalisWrapper {
    private final OxalisOutboundComponent oxalisOutboundComponent;

    public OxalisWrapper() {
        this.oxalisOutboundComponent = new OxalisOutboundComponent();
    }

    OxalisOutboundComponent getOxalisOutboundModule() {
        return oxalisOutboundComponent;
    }

    TransmissionRequestBuilder getTransmissionRequestBuilder(boolean allowOverride) {
        TransmissionRequestBuilder transmissionRequestBuilder = oxalisOutboundComponent.getTransmissionRequestBuilder();
        transmissionRequestBuilder.setTransmissionBuilderOverride(allowOverride);
        return transmissionRequestBuilder;
    }
}
