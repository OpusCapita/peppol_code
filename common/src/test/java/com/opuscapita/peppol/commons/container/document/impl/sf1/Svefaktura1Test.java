package com.opuscapita.peppol.commons.container.document.impl.sf1;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class Svefaktura1Test {
    private final DocumentLoader loader;
    private ErrorHandler errorHandler = mock(ErrorHandler.class);

    public Svefaktura1Test() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        DocumentParser parser = new DocumentParser(factory.newSAXParser(), templates);
        loader = new DocumentLoader(parser);
    }


    @Test
    public void testSvefaktura1() throws Exception {
        DocumentInfo document;

        try (InputStream inputStream = Svefaktura1Test.class.getResourceAsStream("/valid/svefaktura1.xml")) {
            document = loader.load(inputStream, "test", new Endpoint("test", ProcessType.TEST));
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
        assertEquals("urn:sfti:documents:BasicInvoice:1:0::Invoice##urn:sfti:documents:BasicInvoice:1:0::1.0", document.getCustomizationId());
    }

    @Test
    public void testSvefaktura1WithAttachment() throws Exception {
        DocumentInfo document;

        try (InputStream inputStream = Svefaktura1Test.class.getResourceAsStream("/valid/sv1_with_attachment.xml")) {
            document = loader.load(inputStream, "test2", new Endpoint("test", ProcessType.TEST));
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
        assertEquals("urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0",
                document.getCustomizationId());
    }
}
