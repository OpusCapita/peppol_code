package com.opuscapita.peppol.outbound.controller.sender;

import no.difi.vefa.peppol.common.model.Scheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    @Override
    protected Scheme getProcessIdentifierScheme() {
        return Scheme.of("sfti-procid");
    }
}
