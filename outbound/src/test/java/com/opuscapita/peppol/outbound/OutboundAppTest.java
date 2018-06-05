package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by bambr on 16.15.12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OutboundAppTestConfig.class)
public class OutboundAppTest {
    @Autowired
    private OutboundController outboundController;
    @Autowired
    private DocumentLoader documentLoader;

    static StatusReporter statusReporter = mock(StatusReporter.class);
    static ErrorHandler errorHandler = mock(ErrorHandler.class);

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testSvefaktura1DoubleSbdh() throws Exception {
        ContainerMessage cm = new ContainerMessage("meatdata", "file_name", Endpoint.TEST);
        try (InputStream inputStream = OutboundAppTest.class.getResourceAsStream("/sv1_double_SBDH.xml")) {
            DocumentInfo di = documentLoader.load(inputStream, "fileName", Endpoint.TEST);
            assertEquals(Archetype.SVEFAKTURA1, di.getArchetype());
            cm.setDocumentInfo(di);
        }

        outboundController.send(cm);
        assertNotNull(cm.getProcessingInfo());
        assertNotNull(cm.getProcessingInfo().getTransactionId());

        System.out.println(OxalisUtils.getPeppolDocumentTypeId(cm.getDocumentInfo()));
    }

    @Test
    public void testError() throws Exception {
        ContainerMessage cm = createContainerMessage();
        cm.setFileName("-fail-me-");

        try {
            outboundController.send(cm);
            fail("Expected exception not thrown");
        } catch (IllegalStateException expected) {}

    }

    private ContainerMessage createContainerMessage() {
        ContainerMessage cm = new ContainerMessage("meatdata", "file.xml", Endpoint.TEST);
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);
        return cm;
    }

}