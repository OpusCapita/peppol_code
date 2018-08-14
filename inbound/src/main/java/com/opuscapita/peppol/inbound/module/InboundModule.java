package com.opuscapita.peppol.inbound.module;

import com.google.inject.Module;
import no.difi.oxalis.api.persist.PayloadPersister;
import no.difi.oxalis.api.persist.PersisterHandler;
import no.difi.oxalis.api.persist.ReceiptPersister;
import no.difi.oxalis.commons.guice.OxalisModule;

/**
 * Custom Oxalis module that must replace their receiver.
 */
@SuppressWarnings("unused")
public class InboundModule extends OxalisModule implements Module {

    @Override
    protected void configure() {
        bindTyped(PayloadPersister.class, OxalisHandler.class);
        bindTyped(ReceiptPersister.class, OxalisHandler.class);
        bindTyped(PersisterHandler.class, OxalisHandler.class);
    }

}
