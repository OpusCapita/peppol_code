package com.opuscapita.peppol.inbound;

import no.difi.oxalis.api.persist.PayloadPersister;
import no.difi.oxalis.api.persist.PersisterHandler;
import no.difi.oxalis.api.persist.ReceiptPersister;
import no.difi.oxalis.commons.guice.OxalisModule;

@SuppressWarnings("unused")
public class InboundModule extends OxalisModule {

    @Override
    protected void configure() {
        bindTyped(PayloadPersister.class, InboundReceiver.class);
        bindTyped(ReceiptPersister.class, InboundReceiver.class);
        bindTyped(PersisterHandler.class, InboundReceiver.class);
    }
}
