package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        ContainerMessage cm = new ContainerMessage("metadata", "filename1", new Endpoint("test", ProcessType.OUT_FILE_TO_MQ));
        cm.getProcessingInfo().setTransactionId("666");
        cm.setStatus(Endpoint.TEST, "result");

        Route route = new Route();
        route.setEndpoints(Arrays.asList("a", "b", "c"));
        route.setDescription("description");
        route.setMask("mask");
        route.setSource("test");
        cm.getProcessingInfo().setRoute(route);

        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(cm);

        ContainerMessage result = (ContainerMessage) new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray())).readObject();

        assertEquals("test", result.getProcessingInfo().getCurrentEndpoint().getName());
        assertEquals("result", result.getProcessingInfo().getCurrentStatus());
        assertEquals("metadata", result.getProcessingInfo().getSourceMetadata());
        assertEquals("filename1", result.getFileName());
        assertEquals(new Endpoint("test", ProcessType.OUT_FILE_TO_MQ), result.getProcessingInfo().getSource());
        assertEquals("a", result.getProcessingInfo().getRoute().pop());
        assertEquals("description", result.getProcessingInfo().getRoute().getDescription());
        assertEquals("mask", result.getProcessingInfo().getRoute().getMask());
        assertEquals("test", result.getProcessingInfo().getRoute().getSource());
        assertNotNull(result.getDocumentInfo());
    }

}
