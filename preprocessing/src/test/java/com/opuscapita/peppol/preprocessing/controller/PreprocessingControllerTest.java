package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class PreprocessingControllerTest {

    // faked document loader that loads file by name from resources not directly from disk
    private DocumentLoader dl = new DocumentLoader() {
        @NotNull
        @Override
        public BaseDocument load(@NotNull String fileName) throws IOException {
            try (InputStream is = PreprocessingControllerTest.class.getResourceAsStream(fileName)) {
                return load(is, fileName);
            }
        }
    };

    @Test
    public void testValidFile() throws Exception {
        PreprocessingController controller = new PreprocessingController(dl);
        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/valid.xml", Endpoint.PEPPOL));

        assertNotNull(cm);
        assertNotNull(cm.getBaseDocument());
        assertNull(cm.getRoute());
        assertEquals("metadata", cm.getSourceMetadata());
        assertEquals(Endpoint.PEPPOL, cm.getSource());
        assertEquals("9908:923609016", cm.getCustomerId());
    }

    @Test
    public void testInvalidFile() throws Exception {
        PreprocessingController controller = new PreprocessingController(dl);
        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/not_xml.xml", Endpoint.PEPPOL));

        assertNotNull(cm);
        assertNotNull(cm.getBaseDocument());
        assertTrue(cm.getBaseDocument() instanceof InvalidDocument);
        InvalidDocument invalidDocument = (InvalidDocument) cm.getBaseDocument();

        assertEquals("Unable to parse document", invalidDocument.getReason());
    }

    @Test
    public void testNoFile() throws Exception {
        PreprocessingController controller = new PreprocessingController(dl);

        try {
            controller.process(new ContainerMessage("metadata", "_no_such_file", Endpoint.GATEWAY));
            fail();
        } catch (Exception ignore) {}
    }

}