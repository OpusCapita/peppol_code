package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
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
        ContainerMessage cm = new ContainerMessage("metadata", "filename1", new Endpoint("test", Endpoint.Type.GATEWAY));
        cm.setTransactionId("666");
        cm.setStatus("component", "result");

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

        assertEquals("component", result.getProcessingStatus().getComponentName());
        assertEquals("result", result.getProcessingStatus().getResult());
        assertEquals("metadata", result.getSourceMetadata());
        assertEquals("filename1", result.getFileName());
        assertEquals(new Endpoint("test", Endpoint.Type.GATEWAY), result.getSource());
        assertEquals("a", result.getRoute().pop());
        assertEquals("description", result.getRoute().getDescription());
        assertEquals("mask", result.getRoute().getMask());
        assertEquals("test", result.getRoute().getSource());
        assertNotNull(result.getBaseDocument());
    }

}
