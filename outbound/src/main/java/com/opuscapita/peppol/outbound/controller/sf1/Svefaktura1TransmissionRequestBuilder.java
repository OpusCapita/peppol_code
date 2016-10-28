package com.opuscapita.peppol.outbound.controller.sf1;

import eu.peppol.document.NoSbdhParser;
import eu.peppol.document.Sbdh2PeppolHeaderParser;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import eu.peppol.smp.SmpLookupManager;
import eu.peppol.util.GlobalConfiguration;

/**
 * Created by KALNIDA1 on 2016.06.15..
 */
public class Svefaktura1TransmissionRequestBuilder extends TransmissionRequestBuilder {
    public Svefaktura1TransmissionRequestBuilder(
            Sbdh2PeppolHeaderParser sbdh2PeppolHeaderParser, NoSbdhParser noSbdhParser, SmpLookupManager smpLookupManager, GlobalConfiguration globalConfiguration) {
        super(sbdh2PeppolHeaderParser, noSbdhParser, smpLookupManager, globalConfiguration);
    }
    /*
    @Inject
    public Svefaktura1TransmissionRequestBuilder(eu.peppol.document.SbdhParser sbdhParser, NoSbdhParser noSbdhParser, SmpLookupManager smpLookupManager) {
        super(sbdhParser, noSbdhParser, smpLookupManager);
        System.out.println("PeppolTransmissionRequestBuilder instance created");
    }

    @Override
    public TransmissionRequest build() {
        boolean sbdhPresent = hasSbdh(getPayload());
        System.out.println("Has SBDH: "+sbdhPresent);
        if(!sbdhPresent) {
            System.out.println("Absent SBDH in: \n\r"+new String(getPayload()));
        }

        return super.build();
    }

    protected boolean hasSbdh(byte[] payload) {
        return true;
    }

    @Override
    public boolean isOverrideAllowed() {
        return true;
    }
    */
}
