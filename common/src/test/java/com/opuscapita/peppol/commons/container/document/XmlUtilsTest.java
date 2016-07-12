package com.opuscapita.peppol.commons.container.document;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class XmlUtilsTest {

    @Test
    public void testValidDocument() throws Exception {
        try (InputStream inputStream = XmlUtilsTest.class.getResourceAsStream("/valid/ehf_2.0_bii4_no.xml")) {
            Document document = XmlUtils.getDocument(IOUtils.toByteArray(inputStream));
            assertNotNull(document);

            Node root = XmlUtils.getRootNode(document);
            assertNotNull(root);
            assertEquals("Invoice", root.getLocalName());

            Node id = XmlUtils.searchForXPath(root, "PaymentMeans", "ID");
            assertNotNull(id);
            assertEquals("2", id.getTextContent());

            Node notFound = XmlUtils.searchForXPath(id, "something");
            assertNull(notFound);
        }
    }

}