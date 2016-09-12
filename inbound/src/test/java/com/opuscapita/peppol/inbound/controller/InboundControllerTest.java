package com.opuscapita.peppol.inbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class InboundControllerTest {

    @Test
    public void testValidFile() throws Exception {
        String xml = InboundControllerTest.class.getResource("/valid.xml").getPath();
        String baseName = FilenameUtils.removeExtension(xml);

        InboundController controller = new InboundController();
        ContainerMessage cm = controller.processFile(baseName);

        assertNotNull(cm);
        assertNotNull(cm.getDocument());
        assertNull(cm.getRoute());
        assertEquals("metadata", cm.getPeppolMessageMetadata());
        assertEquals(Endpoint.PEPPOL, cm.getSource());
        assertEquals("9908:923609016", cm.getCustomerId());
    }

    @Test
    public void testInvalidFile() throws Exception {
        String xml = InboundControllerTest.class.getResource("/not_xml.xml").getPath();
        String baseName = FilenameUtils.removeExtension(xml);

        InboundController controller = new InboundController();
        ContainerMessage cm = controller.processFile(baseName);

        assertNotNull(cm);
        assertNotNull(cm.getDocument());
        assertTrue(cm.getDocument() instanceof InvalidDocument);
        InvalidDocument invalidDocument = (InvalidDocument) cm.getDocument();

        assertEquals("Unable to parse document", invalidDocument.getReason());
    }

    @Test
    public void testNoFile() throws Exception {
        String xml = "_no_such_file_1284";

        InboundController controller = new InboundController();

        try {
            ContainerMessage cm = controller.processFile(xml);
            fail();
        } catch (Exception ignore) {}
    }

}