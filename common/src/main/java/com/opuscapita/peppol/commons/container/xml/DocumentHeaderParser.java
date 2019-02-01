package com.opuscapita.peppol.commons.container.xml;

import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.sniffer.PeppolStandardBusinessHeader;
import no.difi.oxalis.sniffer.document.HardCodedNamespaceResolver;
import no.difi.oxalis.sniffer.document.PlainUBLHeaderParser;
import no.difi.oxalis.sniffer.document.parsers.PEPPOLDocumentParser;
import no.difi.vefa.peppol.common.model.Header;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * Copied from oxalis "no.difi.oxalis.sniffer.document.NoSbdhParser"
 *
 * Parses UBL based documents, which are not wrapped within an SBDH
 * extracting data and creating a Header.
 */
@Component
public class DocumentHeaderParser {

    private final DocumentBuilderFactory documentBuilderFactory;

    public DocumentHeaderParser() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        try {
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to configure DOM parser for secure processing.", e);
        }
    }

    /**
     * Parses and extracts the data needed to create a PeppolStandardBusinessHeader object. The inputstream supplied
     * should not be wrapped in an SBDH.
     *
     * @param inputStream UBL XML data without an SBDH.
     * @return an instance of Header populated with data from the UBL XML document.
     */
    public Header parse(InputStream inputStream) throws OxalisContentException {
        return originalParse(inputStream).toVefa();
    }

    /**
     * Parses and extracts the data needed to create a PeppolStandardBusinessHeader object. The inputstream supplied
     * should not be wrapped in an SBDH.
     *
     * @param inputStream UBL XML data without an SBDH.
     * @return an instance of PeppolStandardBusinessHeader populated with data from the UBL XML document.
     */
    private PeppolStandardBusinessHeader originalParse(InputStream inputStream) throws OxalisContentException {
        try {

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new HardCodedNamespaceResolver());

            PeppolStandardBusinessHeader sbdh = PeppolStandardBusinessHeader
                    .createPeppolStandardBusinessHeaderWithNewDate();

            // use the plain UBL header parser to decode format and create correct document parser
            PlainUBLHeaderParser headerParser = new PlainUBLHeaderParser(document, xPath);

            // make sure we actually have a UBL type document
            if (headerParser.canParse()) {

                sbdh.setDocumentTypeIdentifier(headerParser.fetchDocumentTypeId().toVefa());
                sbdh.setProfileTypeIdentifier(headerParser.fetchProcessTypeId());

                // try to use a specialized document parser to fetch more document details
                PEPPOLDocumentParser documentParser = null;
                try {
                    documentParser = headerParser.createDocumentParser();
                } catch (Exception ex) {
                    /*
                        allow this to happen so that "unknown" PEPPOL documents still
                        can be used by explicitly setting sender and receiver thru API
                    */
                }
                /* However, if we found an eligible parser, we should be able to determine the sender and receiver */
                if (documentParser != null) {
                    try {
                        sbdh.setSenderId(documentParser.getSender());
                    } catch (Exception e) {
                        // Continue with recipient
                    }
                    try {
                        sbdh.setRecipientId(documentParser.getReceiver());
                    } catch (Exception e) {
                        // Just continue
                    }
                }
            }

            return sbdh;
        } catch (Exception e) {
            throw new OxalisContentException("Unable to parseOld document " + e.getMessage(), e);
        }
    }
}
