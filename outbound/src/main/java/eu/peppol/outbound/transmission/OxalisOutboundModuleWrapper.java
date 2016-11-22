package eu.peppol.outbound.transmission;

import eu.peppol.outbound.OxalisOutboundModule;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.21.11.
 */
@Component
public class OxalisOutboundModuleWrapper {
    OxalisOutboundModule oxalisOutboundModule;

    public OxalisOutboundModuleWrapper() {
        this.oxalisOutboundModule = new OxalisOutboundModule();
    }

    public OxalisOutboundModule getOxalisOutboundModule() {
        return oxalisOutboundModule;
    }

    public TransmissionRequestBuilder getTransmissionRequestBuilder(boolean allowOverride) {
        TransmissionRequestBuilder transmissionRequestBuilder = oxalisOutboundModule.getTransmissionRequestBuilder();
        transmissionRequestBuilder.setTransmissionBuilderOverride(allowOverride);
        return transmissionRequestBuilder;
    }
}
