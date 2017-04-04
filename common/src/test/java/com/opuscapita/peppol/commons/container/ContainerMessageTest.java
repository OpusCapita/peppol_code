package com.opuscapita.peppol.commons.container;

import org.junit.Test;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class ContainerMessageTest {

    @Test
    public void testJsonSerialization() throws Exception {
//        File tmp = File.createTempFile("container_message_test", ".xml");
//        tmp.deleteOnExit();
//        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
//            try (OutputStream os = new FileOutputStream(tmp)) {
//                IOUtils.copy(is, os);
//            }
//        }
//
//        ContainerMessage cm = new ContainerMessage("metadata", tmp.getAbsolutePath(), new Endpoint("test", ProcessType.TEST));
//
//        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
//            DocumentLoader dl = new DocumentLoader();
//            DocumentInfo bd = dl.load(is, tmp.getAbsolutePath());
//            cm.setDocumentInfo(bd);
//        }
//
//        Route r = new Route();
//        r.setEndpoints(Arrays.asList("route.a", "route.b", "route.c"));
//        r.setDescription("test route");
//        r.setMask("*.xml");
//        cm.setRoute(r);
//
//        byte[] bytes = cm.convertToJsonByteArray();
//        String result = new String(bytes);
//
//        ContainerMessage cm2 = ContainerMessage.prepareGson().fromJson(result, ContainerMessage.class);
//        assertEquals(cm.getDocumentInfo().getDocumentId(), cm2.getDocumentInfo().getDocumentId());
//        assertEquals(cm.getSource(), cm2.getSource());
//        assertEquals(cm.getSourceMetadata(), cm2.getSourceMetadata());
//        assertEquals(tmp.getAbsolutePath(), cm2.getFileName());
    }

}
