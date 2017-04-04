package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class ContainerMessageTest {
    private ContainerMessage cm;
    private ContainerMessage cm2;
    private File tmp;

    @Before
    public void before() throws Exception {
        ErrorHandler errorHandler = mock(ErrorHandler.class);
        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentParser parser = new DocumentParser(factory.newSAXParser(), templates);
        DocumentLoader dl = new DocumentLoader(parser);

        tmp = File.createTempFile("container_message_test", ".xml");
        tmp.deleteOnExit();
        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
            try (OutputStream os = new FileOutputStream(tmp)) {
                IOUtils.copy(is, os);
            }
        }

        cm = new ContainerMessage("metadata", tmp.getAbsolutePath(), new Endpoint("test", ProcessType.TEST));
        try (InputStream is = ContainerMessageTest.class.getResourceAsStream("/valid/valid.german.xml")) {
            DocumentInfo bd = dl.load(is, tmp.getAbsolutePath(), new Endpoint("test", ProcessType.TEST));
            cm.setDocumentInfo(bd);
        }

        Route r = new Route();
        r.setEndpoints(Arrays.asList("route.a", "route.b", "route.c"));
        r.setDescription("test route");
        r.setMask("*.xml");
        cm.getProcessingInfo().setRoute(r);
    }

    @Test
    public void testJsonSerialization() throws Exception {

        byte[] bytes = cm.convertToJsonByteArray();
        String result = new String(bytes);

        cm2 = new Gson().fromJson(result, ContainerMessage.class);
    }

    @After
    public void after() throws Exception {
        assertEquals(cm.getDocumentInfo().getDocumentId(), cm2.getDocumentInfo().getDocumentId());
        assertEquals(cm.getProcessingInfo().getSource(), cm2.getProcessingInfo().getSource());
        assertEquals(cm.getProcessingInfo().getSourceMetadata(), cm2.getProcessingInfo().getSourceMetadata());
        assertEquals(tmp.getAbsolutePath(), cm2.getFileName());
    }

    @Test
    public void testObjectSerialization() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(cm);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream inputStream = new ObjectInputStream(in);
        cm2 = (ContainerMessage) inputStream.readObject();
    }

}
