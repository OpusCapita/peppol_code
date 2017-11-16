package com.opuscapita.peppol.commons.container.xml;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import io.vavr.collection.List;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@TestPropertySource(locations = "classpath:application.yml")
public class DocumentParserTest {
    @Autowired
    private DocumentTemplates templates;
    @Value("${peppol.common.consistency_check_enabled}")
    private boolean shouldFailOnInconsistency;

    @Test
    public void testNewFields() throws Exception {
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/BIIXY_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "BIIXY_document_type_test.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

            assertNotNull(result);
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("11:57:14", result.getIssueTime());
            assertEquals("5ba9b74c-292d-4382-996e-04ada2f8eb4e", result.getDocumentBusinessIdentifier());
        }
    }

    @Test
    public void testParseValid() throws Exception {
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/ehf.xml")) {
            DocumentInfo result = parser.parse(inputStream, "ehf.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

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
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/ehf_no_issue_date.xml")) {
            DocumentInfo result = parser.parse(inputStream, "ehf_no_issue_date.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

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
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/BIIXY_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "BIIXY_document_type_test.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

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
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/Catalogue_document_type_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Catalogue_document_type_test.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

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
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/Catalogue_document_type_seller_supplier_test.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Catalogue_document_type_seller_supplier_test.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);

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
    public void testParseSvefakturaNoSbdh() throws Exception {
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/SFTI_svefaktura_BasicInvoice-1.0_Invoice-no-sbdh.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice-no-sbdh.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);
            assertNotNull(result);
            assertEquals(Archetype.INVALID, result.getArchetype());
        }
    }

    @Test
    public void testParseSvefakturaWrongPartyId() throws Exception {
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/SFTI_svefaktura_BasicInvoice-1.0_Invoice-SBDH-senderID-different-SellerParty.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice-SBDH-senderID-different-SellerParty.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);
            assertNotNull(result);
            assertEquals(Archetype.INVALID.equals(result.getArchetype()), shouldFailOnInconsistency);
        }
    }

    @Test
    public void testParseSvefakturaValid() throws Exception {
        DocumentParser parser = createDocumentParser();

        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/SFTI_svefaktura_BasicInvoice-1.0_Invoice.xml")) {
            DocumentInfo result = parser.parse(inputStream, "SFTI_svefaktura_BasicInvoice-1.0_Invoice.xml", new Endpoint("test", ProcessType.TEST), shouldFailOnInconsistency);
            assertNotNull(result);
            assertEquals(Archetype.SVEFAKTURA1, result.getArchetype());
        }
    }

    @NotNull
    private DocumentParser createDocumentParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return new DocumentParser(factory, templates);
    }

    @Test
    public void testMissingSbdhFields() throws Exception {
        DocumentParser parser = createDocumentParser();
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/ehf_no_sbdh_fields.xml")) {
            DocumentInfo result = parser.parse(inputStream, "ehf_no_sbdh_fields.xml", new Endpoint("test", ProcessType.UNKNOWN), false);
            assertNotNull(result);
            List<String> expectedErrorMessages = List.of(
                    "SBDH is missing sender_id field",
                    "SBDH is missing recipient_id field",
                    "PROCESSID is missing or empty",
                    "DOCUMENTID is missing or empty"
            );
            assertEquals(expectedErrorMessages.length(), result.getErrors().size());

            long matchedErrors = result
                    .getErrors()
                    .stream()
                    .map(error -> error.getMessage())
                    .filter(errorMessage -> expectedErrorMessages
                            .toStream()
                            .map(expectedErrorMessage -> errorMessage.contains(expectedErrorMessage))
                            .count(matchingMessage -> matchingMessage) > 0
                    )
                    .count();
            assertEquals(expectedErrorMessages.size(), matchedErrors);
            result.getErrors().forEach(error -> System.out.println(error.getMessage()));
        }
    }

    @Test
    public void testParseNetClientSender() throws Exception {
        DocumentParser parser = createDocumentParser();

        //consistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/special_sender_id/netclient_as_sender.xml")) {
            DocumentInfo result = parser.parse(inputStream, "netclient_as_sender.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().get(0).getMessage().contains(
                    "There are different conflicting values in the document for the field 'sender_id: [9908:995483254, 991723145, 991723145]"
            ));
            assertEquals(Archetype.INVALID, result.getArchetype());
        }
        //inconsistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/special_sender_id/netclient_as_sender.xml")) {
            DocumentInfo result = parser.parse(inputStream, "netclient_as_sender.xml", new Endpoint("test", ProcessType.TEST), false);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertNotEquals(Archetype.INVALID, result.getArchetype());
        }
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void testParseMvaSenderId() throws Exception {
        DocumentParser parser = createDocumentParser();
        //consistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/special_sender_id/9908_989170325MVA-9908_890164072-20170825.xml")) {
            DocumentInfo result = parser.parse(inputStream, "9908_989170325MVA-9908_890164072-20170825.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().get(0).getMessage().contains(
                    "There are different conflicting values in the document for the field 'sender_id: [9908:989170325MVA, 989170325, 989170325]"
            ));
            assertEquals(Archetype.INVALID, result.getArchetype());
        }
        //inconsistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/special_sender_id/9908_989170325MVA-9908_890164072-20170825.xml")) {
            DocumentInfo result = parser.parse(inputStream, "9908_989170325MVA-9908_890164072-20170825.xml", new Endpoint("test", ProcessType.TEST), false);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertNotEquals(Archetype.INVALID, result.getArchetype());
        }
    }

    @Test
    public void testParseSiNoSbdh() throws Exception {
        DocumentParser parser = createDocumentParser();
        //consistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/simpler_invoicing_files/Valid5_no_sbdh.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Valid5_no_sbdh.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertEquals(Archetype.PEPPOL_SI, result.getArchetype());
        }
        //inconsistent check
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/simpler_invoicing_files/Valid5_no_sbdh.xml")) {
            DocumentInfo result = parser.parse(inputStream, "Valid5_no_sbdh.xml", new Endpoint("test", ProcessType.TEST), false);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertEquals(Archetype.PEPPOL_SI, result.getArchetype());
        }
    }

    @Test
    public void testCorruptDocumentIdentifier() throws Exception {
        DocumentParser parser = createDocumentParser();
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/EHF_peppol-bis-5a_invoice-SBDH-DocumentTypeIdentifier-corrupted.xml")) {
            DocumentInfo result = parser.parse(inputStream, "EHF_peppol-bis-5a_invoice-SBDH-DocumentTypeIdentifier-corrupted.xml", new Endpoint("test", ProcessType.UNKNOWN), false);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
        }
    }

    @Test
    public void testParseEhfOrder() throws Exception {
        DocumentParser parser = createDocumentParser();
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/order/EHF-order-sample.xml")) {
            DocumentInfo result = parser.parse(inputStream, "EHF-order-sample.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("Order", result.getDocumentType());
        }
    }

    @Test
    public void testParseEhfOrderResponse() throws Exception {
        DocumentParser parser = createDocumentParser();
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/valid/ordrsp/EHF-order-response.xml")) {
            DocumentInfo result = parser.parse(inputStream, "EHF-order-response.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().isEmpty());
            assertEquals(Archetype.EHF, result.getArchetype());
            assertEquals("OrderResponse", result.getDocumentType());
        }
    }

    @Test
    public void testParseUnrecognizedDocumentType() throws Exception {
        DocumentParser parser = createDocumentParser();
        try (InputStream inputStream = DocumentParserTest.class.getResourceAsStream("/invalid/unrecognized_document_type.xml")) {
            DocumentInfo result = parser.parse(inputStream, "unrecognized_document_type.xml", new Endpoint("test", ProcessType.TEST), true);
            assertNotNull(result);
            assertTrue(result.getErrors().get(0).getMessage().contains("No matching document templates found"));
            assertEquals(Archetype.UNRECOGNIZED, result.getArchetype());
            assertEquals("", result.getDocumentType());
        }
    }
}
