package com.opuscapita.peppol.commons.container.xml;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
public class DocumentParserTest {
    @Autowired
    private DocumentTemplates templates;

    @Test
    public void testNewFields() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/BIIXY_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "/valid/BIIXY_document_type_test.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("11:57:14", result.getIssueTime());
            assertEquals("5ba9b74c-292d-4382-996e-04ada2f8eb4e", result.getDocumentBusinessIdentifier());
        }
    }

    @Test
    public void testParseValid() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

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
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

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
    @SuppressWarnings("Duplicates")
    @Test
    public void testParseEhfInvoiceBiiyx() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/BIIXY_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "BIIXY_document_type_test.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("9908:914113172", result.getSenderId());
            assertEquals("9908:884205662", result.getRecipientId());
            assertTrue(result.getErrors().isEmpty());
            assertTrue(result.getWarnings().isEmpty());
            assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", result.getRootNameSpace());
            assertEquals("Invoice", result.getRootNodeName());
            assertEquals("Invoice", result.getDocumentType());
            assertEquals("2.1", result.getVersionId());
            assertEquals("urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.cenbii.eu:profile.eu:biixy:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0",
                    result.getCustomizationId());
            assertEquals("urn:www.cenbii.eu:profile:biixy:ver2.0", result.getProfileId());
            assertEquals("B. Braun Medical A/S", result.getSenderName());
            assertEquals("NorEngros Kjosavik AS", result.getRecipientName());
            assertEquals("NO", result.getSenderCountryCode());
            assertEquals("NO", result.getRecipientCountryCode());
            assertEquals("2017-04-26", result.getIssueDate());
        }

    }

    @Test
    public void testParseEhfCatalogue() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/Catalogue_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Catalogue_document_type_test.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("9908:982789761", result.getSenderId());
            assertEquals("9908:972418013", result.getRecipientId());
            assertTrue(result.getErrors().isEmpty());
            assertTrue(result.getWarnings().isEmpty());
            assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2", result.getRootNameSpace());
            assertEquals("Catalogue", result.getRootNodeName());
            assertEquals("Catalogue", result.getDocumentType());
            assertEquals("2.1", result.getVersionId());
            assertEquals("urn:www.cenbii.eu:transaction:biitrns019:ver2.0:extended:urn:www.peppol.eu:bis:peppol1a:ver2.0:extended:urn:www.difi.no:ehf:katalog:ver1.0",
                    result.getCustomizationId());
            assertEquals("urn:www.cenbii.eu:profile:bii01:ver2.0", result.getProfileId());
            assertEquals("Helseservice Engros AS", result.getSenderName());
            assertEquals("2017-04-26", result.getIssueDate());
        }

    }

    @Test
    public void testParseEhfCatalogueSellerSuplier() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/Catalogue_document_type_seller_supplier_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Catalogue_document_type_seller_supplier_test.xml", new Endpoint("test", ProcessType.TEST));

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("9908:982789761", result.getSenderId());
            assertEquals("9908:972418013", result.getRecipientId());
            assertTrue(result.getErrors().isEmpty());
            assertTrue(result.getWarnings().isEmpty());
            assertEquals("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2", result.getRootNameSpace());
            assertEquals("Catalogue", result.getRootNodeName());
            assertEquals("Catalogue", result.getDocumentType());
            assertEquals("2.1", result.getVersionId());
            assertEquals("urn:www.cenbii.eu:transaction:biitrns019:ver2.0:extended:urn:www.peppol.eu:bis:peppol1a:ver2.0:extended:urn:www.difi.no:ehf:katalog:ver1.0",
                    result.getCustomizationId());
            assertEquals("urn:www.cenbii.eu:profile:bii01:ver2.0", result.getProfileId());
            assertEquals("Helseservice Engros AS", result.getSenderName());
            assertEquals("2017-04-26", result.getIssueDate());
        }

    }

    @Test
    public void testParseSvefakturaNoSbdh() throws Exception{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/SFTI_svefaktura_BasicInvoice-1.0_Invoice-no-sbdh.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice-no-sbdh.xml", new Endpoint("test", ProcessType.TEST));
            assertNotNull(result);
            assertEquals(Archetype.INVALID, result.getArchetype());
        }
    }

    @Test
    public void testParseSvefakturaWrongPartyId() throws Exception{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/SFTI_svefaktura_BasicInvoice-1.0_Invoice-SBDH-senderID-different-SellerParty.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice-SBDH-senderID-different-SellerParty.xml", new Endpoint("test", ProcessType.TEST));
            assertNotNull(result);
            assertEquals(Archetype.INVALID, result.getArchetype());
        }
    }

    @Test
    public void testParseSvefakturaValid() throws Exception{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory, templates);

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/SFTI_svefaktura_BasicInvoice-1.0_Invoice.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice.xml", new Endpoint("test", ProcessType.TEST));
            assertNotNull(result);
            assertEquals(Archetype.SVEFAKTURA1, result.getArchetype());
        }
    }
}
