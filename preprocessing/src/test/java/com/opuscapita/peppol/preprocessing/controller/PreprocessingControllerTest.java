package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.storage.Storage;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
public class PreprocessingControllerTest {
    private final static Endpoint ENDPOINT = new Endpoint("test", Endpoint.Type.PEPPOL);

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
        Storage storage = mock(Storage.class);
        when(storage.moveToLongTerm("9908:980361330", "9908:923609016", "/valid.xml")).thenReturn("long_term");

        PreprocessingController controller = new PreprocessingController(dl, storage, null);
        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/valid.xml", ENDPOINT));

        assertNotNull(cm);
        assertNotNull(cm.getBaseDocument());
        assertNull(cm.getRoute());
        assertEquals("metadata", cm.getSourceMetadata());
        assertEquals(ENDPOINT, cm.getSource());
        assertEquals("9908:923609016", cm.getCustomerId());
        assertEquals("long_term", cm.getFileName());
    }

    @Test
    public void testInvalidFile() throws Exception {
        Storage storage = mock(Storage.class);
        when(storage.moveToLongTerm("", "", "/not_xml.xml")).thenReturn("long_term");

        PreprocessingController controller = new PreprocessingController(dl, storage, null);
        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/not_xml.xml", ENDPOINT));

        assertNotNull(cm);
        assertNotNull(cm.getBaseDocument());
        assertTrue(cm.getBaseDocument() instanceof InvalidDocument);
        InvalidDocument invalidDocument = (InvalidDocument) cm.getBaseDocument();
        assertEquals("Unable to parse document", invalidDocument.getReason());
        assertEquals("long_term", cm.getFileName());
    }

    @Test
    public void testNoFile() throws Exception {
        Storage storage = mock(Storage.class);
        PreprocessingController controller = new PreprocessingController(dl, storage, null);

        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "_no_such_file", ENDPOINT));
        assertTrue(cm.getBaseDocument() instanceof InvalidDocument);
    }

}
