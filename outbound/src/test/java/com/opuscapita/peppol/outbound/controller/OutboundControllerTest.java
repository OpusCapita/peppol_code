package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.FakeSender;
import com.opuscapita.peppol.outbound.controller.sender.Svefaktura1Sender;
import com.opuscapita.peppol.outbound.controller.sender.TestSender;
import com.opuscapita.peppol.outbound.controller.sender.UblSender;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Sergejs.Roze
 */
public class OutboundControllerTest {
    private MessageQueue messageQueue = mock(MessageQueue.class);
    private UblSender ublSender = mock(UblSender.class);
    private FakeSender fakeSender = mock(FakeSender.class);
    private Svefaktura1Sender svefaktura1Sender = mock(Svefaktura1Sender.class);
    private TestSender testSender = mock(TestSender.class);
    private OxalisErrorRecognizer oxalisErrorRecognizer = mock(OxalisErrorRecognizer.class);

    @Test
    public void testRetries() throws Exception {
        when(fakeSender.send(any())).thenThrow(new IOException("test exception"));

        OutboundController controller =
                new OutboundController(messageQueue, ublSender, fakeSender, svefaktura1Sender, testSender, oxalisErrorRecognizer);
        controller.setComponentName("component_name");
        when(oxalisErrorRecognizer.recognize(any(Exception.class))).thenReturn(SendingErrors.CONNECTION_ERROR);

        ContainerMessage cm = new ContainerMessage("meatdata", "filename", new Endpoint("test", ProcessType.TEST));
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);

        Route route = new Route();
        route.getEndpoints().add("next");
        assertNotNull(cm.getProcessingInfo());
        cm.getProcessingInfo().setRoute(route);

        controller.send(cm);

        assertEquals(ProcessType.OUT_PEPPOL_RETRY, cm.getProcessingInfo().getCurrentEndpoint().getType());
        verifyZeroInteractions(ublSender);
        verifyZeroInteractions(testSender);
        verifyZeroInteractions(svefaktura1Sender);
        verify(messageQueue).convertAndSend("next", cm);
    }

    @Test
    public void testNoRetries() throws Exception {
        when(fakeSender.send(any())).thenThrow(new IOException("test exception"));

        OutboundController controller =
                new OutboundController(messageQueue, ublSender, fakeSender, svefaktura1Sender, testSender, oxalisErrorRecognizer);
        controller.setComponentName("component_name");
        when(oxalisErrorRecognizer.recognize(any(Exception.class))).thenReturn(SendingErrors.UNKNOWN_RECIPIENT);

        ContainerMessage cm = new ContainerMessage("meatdata", "filename", new Endpoint("test", ProcessType.TEST));
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);

        Route route = new Route();
        route.getEndpoints().add("next");
        route.setDescription("test route");
        assertNotNull(cm.getProcessingInfo());
        cm.getProcessingInfo().setRoute(route);

        try {
            controller.send(cm);
            fail("This method must throw an exception");
        } catch (Exception e) {
            assertEquals(ProcessType.OUT_OUTBOUND, cm.getProcessingInfo().getCurrentEndpoint().getType());
            assertEquals("test exception", cm.getProcessingInfo().getProcessingException());
        }

        verifyZeroInteractions(ublSender);
        verifyZeroInteractions(testSender);
        verifyZeroInteractions(svefaktura1Sender);
        verifyZeroInteractions(messageQueue);
    }
}
