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
        try (InputStream inputStream = DocumentUtilsTest.class.getResourceAsStream("/valid/532c79be-8738-4b35-8257-b9efacb1e7fe.xml")) {
            Document document = DocumentUtils.getDocument(IOUtils.toByteArray(inputStream));
            assertNotNull(document);

            Node root = DocumentUtils.getRootNode(document);
            assertNotNull(root);
            assertEquals("Invoice", root.getLocalName());

            Node id = DocumentUtils.searchForXPath(root, "PaymentMeans", "PaymentID");
            assertNotNull(id);
            assertEquals("205852817", id.getTextContent());

            Node notFound = DocumentUtils.searchForXPath(id, "something");
            assertNull(notFound);
        }
    }

}
