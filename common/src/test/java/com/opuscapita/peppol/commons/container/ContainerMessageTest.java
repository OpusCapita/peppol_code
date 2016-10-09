package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class ContainerMessageTest {

    @Test
    public void testSerialization() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "test.xml", Endpoint.PEPPOL);

        byte[] bytes = cm.getBytes();
        String result = new String(bytes);

        ContainerMessage cm2 = new Gson().fromJson(result, ContainerMessage.class);
        assertEquals(cm.getBaseDocument(), cm2.getBaseDocument());
        assertEquals(cm.getSource(), cm2.getSource());
        assertEquals(cm.getSourceMetadata(), cm2.getSourceMetadata());
        assertEquals("test.xml", cm2.getFileName());
    }

}