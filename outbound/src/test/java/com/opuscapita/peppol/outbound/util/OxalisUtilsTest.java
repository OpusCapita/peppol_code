package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OxalisUtilsTest {

    @Test
    public void fixIfSvefakturaObjectEnvelope() throws PeppolParsingException {
        String customizationId = "urn:sfti:services:documentprocessing:BasicInvoice:1:0" +
                "###urn:sfti:documents:StandardBusinessDocumentHeader::Invoice" +
                "##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope";
        String expected = "busdox-docid-qns::urn:sfti:documents:StandardBusinessDocumentHeader::Invoice" +
                "##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0";
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setCustomizationId(customizationId);
        String result = OxalisUtils.getPeppolDocumentTypeId(documentInfo).toString();
        assertEquals(expected, result);

    }
}