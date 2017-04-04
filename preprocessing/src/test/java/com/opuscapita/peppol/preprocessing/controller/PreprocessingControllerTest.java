package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.junit.Test;

/**
 * @author Sergejs.Roze
 */
public class PreprocessingControllerTest {
    private final static Endpoint ENDPOINT = new Endpoint("test", ProcessType.TEST);

    // faked document loader that loads file by name from resources not directly from disk
    // TODO
//    private DocumentLoader dl = new DocumentLoader(parser) {
//        @NotNull
//        @Override
//        public DocumentInfo load(@NotNull String fileName) throws IOException {
//            try (InputStream is = PreprocessingControllerTest.class.getResourceAsStream(fileName)) {
//                return load(is, fileName);
//            }
//        }
//    };

    @Test
    public void testValidFile() throws Exception {
//        Storage storage = mock(Storage.class);
//        when(storage.moveToLongTerm("9908:980361330", "9908:923609016", "/valid.xml")).thenReturn("long_term");
//
//        PreprocessingController controller = new PreprocessingController(dl, storage);
//        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/valid.xml", ENDPOINT));
//
//        assertNotNull(cm);
//        assertNotNull(cm.getDocumentInfo());
//        assertNull(cm.getRoute());
//        assertEquals("metadata", cm.getSourceMetadata());
//        assertEquals(ENDPOINT, cm.getSource());
//        assertEquals("9908:980361330", cm.getCustomerId());
//        assertEquals("long_term", cm.getFileName());
    }

    @Test
    public void testInvalidFile() throws Exception {
//        Storage storage = mock(Storage.class);
//        when(storage.moveToLongTerm("", "", "/not_xml.xml")).thenReturn("long_term");
//
//        PreprocessingController controller = new PreprocessingController(dl, storage);
//        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "/not_xml.xml", ENDPOINT));
//
//        assertNotNull(cm);
//        assertNotNull(cm.getDocumentInfo());
//        assertTrue(cm.getDocumentInfo() instanceof InvalidDocument);
//        InvalidDocument invalidDocument = (InvalidDocument) cm.getDocumentInfo();
//        assertEquals("Unable to parse document", invalidDocument.getReason());
//        assertEquals("long_term", cm.getFileName());
    }

    @Test
    public void testNoFile() throws Exception {
//        Storage storage = mock(Storage.class);
//        PreprocessingController controller = new PreprocessingController(dl, storage);
//
//        ContainerMessage cm = controller.process(new ContainerMessage("metadata", "_no_such_file", ENDPOINT));
//        assertTrue(cm.getDocumentInfo() instanceof InvalidDocument);
    }

}
