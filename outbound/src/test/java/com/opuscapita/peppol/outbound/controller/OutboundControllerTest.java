package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.FakeSender;
import com.opuscapita.peppol.outbound.controller.sender.Svefaktura1Sender;
import com.opuscapita.peppol.outbound.controller.sender.TestSender;
import com.opuscapita.peppol.outbound.controller.sender.UblSender;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    @Test
    public void testRetries() throws Exception {
        when(fakeSender.send(any())).thenThrow(new IOException("test exception"));

        OutboundController controller = new OutboundController(messageQueue, ublSender, fakeSender, svefaktura1Sender, testSender);
        controller.setComponentName("component_name");

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

}