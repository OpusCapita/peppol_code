package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.document.impl.SveFaktura1Document;
import com.opuscapita.peppol.commons.container.document.impl.UblDocument;
import com.opuscapita.peppol.commons.container.document.test.TestTypeOne;
import com.opuscapita.peppol.commons.container.document.test.TestTypeTwo;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class DocumentLoaderTest {

    @Test
    public void testDocumentLoader() throws Exception {
        Set<BaseDocument> result = DocumentLoader.reloadDocumentTypes("com.opuscapita.peppol.commons.container.document.test");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.toArray()[0].getClass() == TestTypeOne.class ||
                result.toArray()[0].getClass() == TestTypeTwo.class);
        assertTrue(result.toArray()[1].getClass() == TestTypeOne.class ||
                result.toArray()[1].getClass() == TestTypeTwo.class);
    }

    @Test
    public void testTypes() throws Exception {
        DocumentLoader loader = new DocumentLoader();
        BaseDocument document;

        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/ehf.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof UblDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/ehf_2.0_bii4_no.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof UblDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/ehf_2.0_bii5_no.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof UblDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/valid.german.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof UblDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/valid_sbdh_2.1.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof UblDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/svefaktura1.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof SveFaktura1Document);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/invalid/not_xml.txt")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof InvalidDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/invalid/random.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof InvalidDocument);
        }
        try (InputStream inputStream = DocumentLoaderTest.class.getResourceAsStream("/valid/sv1_with_attachment.xml")) {
            document = loader.load(inputStream, "test");
            assertTrue(document instanceof SveFaktura1Document);
        }
    }

}
