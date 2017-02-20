package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.ProcessType;
import com.opuscapita.peppol.commons.container.route.Route;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
public class SerializationTest {

    // since we serialize objects using standard Java, let's check that ContainerMessage can really be serialized
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testSerialization() throws Exception {
        Endpoint endpoint = new Endpoint("test", ProcessType.TEST);

        ContainerMessage cm = new ContainerMessage("metadata", "filename1", new Endpoint("test", ProcessType.OUT_FILE_TO_MQ));
        cm.setTransactionId("666");
        cm.setStatus(endpoint, "result");

        Route route = new Route();
        route.setEndpoints(Arrays.asList("a", "b", "c"));
        route.setDescription("description");
        route.setMask("mask");
        route.setSource("test");
        cm.setRoute(route);

        try (InputStream is = SerializationTest.class.getResourceAsStream("/valid/ehf.xml")) {
            cm.setBaseDocument(new DocumentLoader().load(is, "filename2"));
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(cm);

        ContainerMessage result = (ContainerMessage) new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray())).readObject();

        assertEquals("test", result.getProcessingStatus().getEndpoint().getName());
        assertEquals("result", result.getProcessingStatus().getResult());
        assertEquals("metadata", result.getSourceMetadata());
        assertEquals("filename1", result.getFileName());
        assertEquals(new Endpoint("test", ProcessType.OUT_FILE_TO_MQ), result.getSource());
        assertEquals("a", result.getRoute().pop());
        assertEquals("description", result.getRoute().getDescription());
        assertEquals("mask", result.getRoute().getMask());
        assertEquals("test", result.getRoute().getSource());
        assertNotNull(result.getBaseDocument());
    }

}
