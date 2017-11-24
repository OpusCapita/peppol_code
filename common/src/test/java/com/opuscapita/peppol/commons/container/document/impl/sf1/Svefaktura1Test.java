package com.opuscapita.peppol.commons.container.document.impl.sf1;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Svefaktura1Test {
    @Autowired
    private DocumentLoader loader;

    @Test
    public void testSvefaktura1() throws Exception {
        DocumentInfo document;

        try (InputStream inputStream = Svefaktura1Test.class.getResourceAsStream("/valid/svefaktura1.xml")) {
            document = loader.load(inputStream, "test", Endpoint.TEST);
            assertEquals(Archetype.SVEFAKTURA1, document.getArchetype());
        }

        assertEquals("0008:7381034999991", document.getSenderId());
        assertEquals("0007:2021002361", document.getRecipientId());
        assertEquals("60445647", document.getDocumentId());
        assertEquals("1.0", document.getVersionId());
        assertEquals("2016-04-13", document.getIssueDate());
        assertEquals("2016-05-18", document.getDueDate());
        assertEquals("LÄNSSTYRELSEN", document.getRecipientName());
        assertEquals("SE", document.getSenderCountryCode());
        assertEquals("SE", document.getRecipientCountryCode());
        assertEquals("urn:sfti:services:documentprocessing:BasicInvoice:1:0", document.getProfileId());
        assertEquals("urn:sfti:documents:BasicInvoice:1:0", document.getCustomizationId());
    }

    @Test
    public void testSvefaktura1WithAttachment() throws Exception {
        DocumentInfo document;

        try (InputStream inputStream = Svefaktura1Test.class.getResourceAsStream("/valid/sv1_with_attachment.xml")) {
            document = loader.load(inputStream, "test2", Endpoint.TEST);
            assertEquals(Archetype.SVEFAKTURA1, document.getArchetype());
        }

        assertEquals("0008:1234567890123", document.getSenderId());
        assertEquals("0008:1234567890123", document.getRecipientId());
        assertEquals("310791199", document.getDocumentId());
        assertEquals("1.0", document.getVersionId());
        assertEquals("2016-07-21", document.getIssueDate());
        assertEquals("2016-08-26", document.getDueDate());
        assertEquals("Skanska Asfalt och Betong AB", document.getSenderName());
        assertEquals("TRAFIKVERKET", document.getRecipientName());
        assertEquals("SE", document.getSenderCountryCode());
        assertEquals("SE", document.getRecipientCountryCode());
        assertEquals("urn:sfti:services:documentprocessing:BasicInvoice:1:0", document.getProfileId());
        assertEquals("urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0",
                document.getCustomizationId());
    }
}
