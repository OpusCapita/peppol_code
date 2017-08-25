package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import eu.peppol.outbound.transmission.OxalisOutboundModuleWrapper;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
public class Svefaktura1SenderTest {
    private OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper = mock(OxalisOutboundModuleWrapper.class);
    private TransmissionRequestBuilder requestBuilder = mock(TransmissionRequestBuilder.class);

    @Ignore
    @Test
    public void testSvefakturaData() throws Exception {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            return;
        }

        when(oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true)).thenReturn(requestBuilder);

        Svefaktura1Sender sender = new Svefaktura1Sender(oxalisOutboundModuleWrapper);

        ContainerMessage cm = new ContainerMessage("meatdata", createTestFile(), new Endpoint("test", ProcessType.TEST));
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);

        Route route = new Route();
        route.getEndpoints().add("next");
        assertNotNull(cm.getProcessingInfo());
        cm.getProcessingInfo().setRoute(route);

        assertNotNull(cm.getDocumentInfo());
        cm.getDocumentInfo().setRootNameSpace("cac");
        cm.getDocumentInfo().setRootNodeName("Invoice");

        sender.send(cm);
    }

    private String createTestFile() throws IOException {
        File tmp = File.createTempFile("svefaktura-unit-test-", "");

        try (InputStream inputStream = Svefaktura1SenderTest.class.getResourceAsStream("/Valid1.xml")) {
            IOUtils.copy(inputStream, new FileOutputStream(tmp));
        }

        tmp.deleteOnExit();

        return tmp.getAbsolutePath();
    }

}
