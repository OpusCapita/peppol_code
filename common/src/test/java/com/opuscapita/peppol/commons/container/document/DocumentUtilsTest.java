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
public class DocumentUtilsTest {

    @Test
    public void testValidDocument() throws Exception {
        try (InputStream inputStream = DocumentUtilsTest.class.getResourceAsStream("/valid/ehf_2.0_bii4_no.xml")) {
            Document document = DocumentUtils.getDocument(IOUtils.toByteArray(inputStream));
            assertNotNull(document);

            Node root = DocumentUtils.getRootNode(document);
            assertNotNull(root);
            assertEquals("Invoice", root.getLocalName());

            Node id = DocumentUtils.searchForXPath(root, "PaymentMeans", "ID");
            assertNotNull(id);
            assertEquals("2", id.getTextContent());

            Node notFound = DocumentUtils.searchForXPath(id, "something");
            assertNull(notFound);
        }
    }

}
