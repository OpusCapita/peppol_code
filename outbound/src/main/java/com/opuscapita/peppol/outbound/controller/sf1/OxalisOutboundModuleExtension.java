package com.opuscapita.peppol.outbound.controller.sf1;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.smp.SmpModule;
import eu.peppol.util.OxalisCommonsModule;

/**
 * Created by KALNIDA1 on 2016.06.15..
 */
public class OxalisOutboundModuleExtension extends OxalisOutboundModule {
    Injector injector = Guice.createInjector(new OxalisCommonsModule(), new SmpModule());

    public Svefaktura1TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return this.injector.getInstance(Svefaktura1TransmissionRequestBuilder.class);
    }
}
