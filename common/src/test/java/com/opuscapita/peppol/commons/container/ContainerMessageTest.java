package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class ContainerMessageTest {

    @Test
    public void testJsonSerialization() throws Exception {
        File tmp = File.createTempFile("container_message_test", ".xml");
        tmp.deleteOnExit();
        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
            try (OutputStream os = new FileOutputStream(tmp)) {
                IOUtils.copy(is, os);
            }
        }

        ContainerMessage cm = new ContainerMessage("metadata", tmp.getAbsolutePath(), Endpoint.PEPPOL);

        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
            DocumentLoader dl = new DocumentLoader();
            BaseDocument bd = dl.load(is, tmp.getAbsolutePath());
            cm.setBaseDocument(bd);
        }

        Route r = new Route();
        r.setEndpoints(Arrays.asList("route.a", "route.b", "route.c"));
        r.setDescription("test route");
        r.setMask("*.xml");
        cm.setRoute(r);

        byte[] bytes = cm.convertToJsonByteArray();
        String result = new String(bytes);

        ContainerMessage cm2 = ContainerMessage.prepareGson().fromJson(result, ContainerMessage.class);
        assertEquals(cm.getBaseDocument().getDocumentId(), cm2.getBaseDocument().getDocumentId());
        assertEquals(cm.getSource(), cm2.getSource());
        assertEquals(cm.getSourceMetadata(), cm2.getSourceMetadata());
        assertEquals(tmp.getAbsolutePath(), cm2.getFileName());
    }

}
