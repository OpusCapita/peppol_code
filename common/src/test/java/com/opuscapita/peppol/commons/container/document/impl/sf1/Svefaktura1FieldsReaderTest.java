package com.opuscapita.peppol.commons.container.document.impl.sf1;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.SveFaktura1Document;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class Svefaktura1FieldsReaderTest {

    @Test
    public void testSvefaktura1() throws Exception {
        BaseDocument document;

        try (InputStream inputStream = Svefaktura1FieldsReaderTest.class.getResourceAsStream("/valid/svefaktura1.xml")) {
            document = new DocumentLoader().load(inputStream, "test");
            assertTrue(document instanceof SveFaktura1Document);
        }

        assertEquals("0008:7381034999991", document.getSenderId());
        assertEquals("0007:2021002361", document.getRecipientId());
        assertEquals("60445647", document.getDocumentId());
        assertEquals("test", document.getFileName());
        assertEquals("1.0", document.getVersionId());
        assertEquals("2016-04-13", document.getIssueDate());
        assertEquals("2016-05-18", document.getDueDate());
        assertEquals("Martin & Servera AB", document.getSenderName());
        assertEquals("LÄNSSTYRELSEN", document.getRecipientName());
        assertEquals("SE", document.getSenderCountryCode());
        assertEquals("SE", document.getRecipientCountryCode());
        assertEquals("urn:sfti:services:documentprocessing:BasicInvoice:1:0", document.getProfileId());
        assertEquals("urn:sfti:documents:BasicInvoice:1:0::Invoice##urn:sfti:documents:BasicInvoice:1:0::1.0", document.getCustomizationId());
    }

    @Test
    public void testSvefaktura1WithAttachment() throws Exception {
        BaseDocument document;

        try (InputStream inputStream = Svefaktura1FieldsReaderTest.class.getResourceAsStream("/valid/sv1_with_attachment.xml")) {
            document = new DocumentLoader().load(inputStream, "test2");
            assertTrue(document instanceof SveFaktura1Document);
        }

        assertEquals("0008:1234567890123", document.getSenderId());
        assertEquals("0008:1234567890123", document.getRecipientId());
        assertEquals("310791199", document.getDocumentId());
        assertEquals("test2", document.getFileName());
        assertEquals("1.0", document.getVersionId());
        assertEquals("2016-07-21", document.getIssueDate());
        assertEquals("2016-08-26", document.getDueDate());
        assertEquals("Skanska Asfalt och Betong AB", document.getSenderName());
        assertEquals("TRAFIKVERKET", document.getRecipientName());
        assertEquals("SE", document.getSenderCountryCode());
        assertEquals("SE", document.getRecipientCountryCode());
        assertEquals("urn:sfti:services:documentprocessing:BasicInvoice:1:0", document.getProfileId());
        assertEquals("urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0",
                document.getCustomizationId());
    }
}
