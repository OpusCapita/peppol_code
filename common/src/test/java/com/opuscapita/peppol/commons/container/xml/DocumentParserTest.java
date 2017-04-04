package com.opuscapita.peppol.commons.container.xml;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class DocumentParserTest {
    private ErrorHandler errorHandler = mock(ErrorHandler.class);

    @Test
    public void testParseValid() throws Exception {
        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory.newSAXParser(), templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/ehf.xml")) {
            DocumentInfo result = parser.parse(inputStream, "ehf.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("9908:980361330", result.getSenderId());
            assertEquals("9908:923609016", result.getRecipientId());
            assertTrue(result.getErrors().isEmpty());
            assertTrue(result.getWarnings().isEmpty());
            assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", result.getRootNameSpace());
            assertEquals("Invoice", result.getRootNodeName());
            assertEquals("Invoice", result.getDocumentType());
            assertEquals("2.1", result.getVersionId());
            assertEquals("urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0",
                    result.getCustomizationId());
            assertEquals("urn:www.cenbii.eu:profile:bii04:ver2.0", result.getProfileId());
            assertEquals("PDS Protek AS", result.getSenderName());
            assertEquals("Statoil ASA", result.getRecipientName());
            assertEquals("NO", result.getSenderCountryCode());
            assertEquals("NO", result.getRecipientCountryCode());
            assertEquals("2016-05-31", result.getIssueDate());
            assertEquals("2016-06-30", result.getDueDate());
        }

    }

    @Test
    public void testParseAlmostValid() throws Exception {
        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory.newSAXParser(), templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/ehf_no_issue_date.xml")) {
            DocumentInfo result = parser.parse(inputStream, "ehf_no_issue_date.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.INVALID, result.getArchetype());
            assertEquals("9908:980361330", result.getSenderId());
            assertEquals("9908:923609016", result.getRecipientId());
            assertEquals(1, result.getErrors().size());
            assertTrue(result.getWarnings().isEmpty());
            assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", result.getRootNameSpace());
            assertEquals("Invoice", result.getRootNodeName());
            assertEquals("Invoice", result.getDocumentType());
            assertEquals("2.1", result.getVersionId());
            assertEquals("urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0",
                    result.getCustomizationId());
            assertEquals("urn:www.cenbii.eu:profile:bii04:ver2.0", result.getProfileId());
            assertEquals("PDS Protek AS", result.getSenderName());
            assertEquals("Statoil ASA", result.getRecipientName());
            assertEquals("NO", result.getSenderCountryCode());
            assertEquals("NO", result.getRecipientCountryCode());
            assertEquals("", result.getIssueDate());
            assertEquals("2016-06-30", result.getDueDate());
        }

    }
}
