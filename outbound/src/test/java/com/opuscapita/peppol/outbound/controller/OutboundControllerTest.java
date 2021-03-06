package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.FakeSender;
import com.opuscapita.peppol.outbound.controller.sender.RealSender;
import com.opuscapita.peppol.outbound.controller.sender.TestSender;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("Duplicates")
public class OutboundControllerTest {

    private RealSender realSender = mock(RealSender.class);
    private FakeSender fakeSender = mock(FakeSender.class);
    private TestSender testSender = mock(TestSender.class);
    private MessageQueue messageQueue = mock(MessageQueue.class);
    private ErrorHandler errorHandler = mock(ErrorHandler.class);
    private StatusReporter statusReporter = mock(StatusReporter.class);
    private OxalisErrorRecognizer oxalisErrorRecognizer = mock(OxalisErrorRecognizer.class);

    @Test
    public void testRetries() throws Exception {
        OutboundController controller = new OutboundController(realSender, fakeSender, testSender, messageQueue, oxalisErrorRecognizer, statusReporter, errorHandler);
        controller.setEmailNotificatorQueue("email_notificator");
        controller.setComponentName("component_name");

        when(fakeSender.send(any())).thenThrow(new IOException("test exception"));
        when(oxalisErrorRecognizer.recognize(any(Exception.class))).thenReturn(SendingErrors.RECEIVING_AP_ERROR);

        ContainerMessage cm = createContainerMessage();
        controller.send(cm);

        assertEquals(ProcessType.OUT_PEPPOL_RETRY, cm.getProcessingInfo().getCurrentEndpoint().getType());
        verifyZeroInteractions(realSender);
        verifyZeroInteractions(testSender);
        verifyZeroInteractions(errorHandler);
        verifyZeroInteractions(statusReporter);
        verify(messageQueue).convertAndSend("next", cm);
    }

    @Test
    public void testNotRetriableError() throws Exception {
        OutboundController controller = new OutboundController(realSender, fakeSender, testSender, messageQueue, oxalisErrorRecognizer, statusReporter, errorHandler);
        controller.setEmailNotificatorQueue("email_notificator");
        controller.setComponentName("component_name");

        Exception e = new IOException("test exception");
        when(fakeSender.send(any())).thenThrow(e);
        when(oxalisErrorRecognizer.recognize(any(Exception.class))).thenReturn(SendingErrors.SECURITY_ERROR);

        ContainerMessage cm = createContainerMessage();
        controller.send(cm);

        assertEquals(ProcessType.OUT_OUTBOUND, cm.getProcessingInfo().getCurrentEndpoint().getType());
        assertEquals(cm.getProcessingInfo().getProcessingException(), e.getMessage());
        verifyZeroInteractions(realSender);
        verifyZeroInteractions(testSender);
        verifyZeroInteractions(messageQueue); // no event sent to email-notificator
        verify(statusReporter).reportError(cm, e); // event sent to eventing module
        verify(errorHandler).reportWithContainerMessage(cm, e, e.getMessage()); // event sent for ticket creation
    }

    @Test
    public void testEmailNotifierError() throws Exception {
        OutboundController controller = new OutboundController(realSender, fakeSender, testSender, messageQueue, oxalisErrorRecognizer, statusReporter, errorHandler);
        controller.setEmailNotificatorQueue("email_notificator");
        controller.setComponentName("component_name");

        Exception e = new IOException("test exception");
        when(fakeSender.send(any())).thenThrow(e);
        when(oxalisErrorRecognizer.recognize(any(Throwable.class))).thenReturn(SendingErrors.UNKNOWN_RECIPIENT);

        ContainerMessage cm = createContainerMessage();
        controller.send(cm);

        assertEquals(ProcessType.OUT_OUTBOUND, cm.getProcessingInfo().getCurrentEndpoint().getType());
        assertEquals(cm.getProcessingInfo().getProcessingException(), e.getMessage());
        verifyZeroInteractions(realSender);
        verifyZeroInteractions(testSender);
        verifyZeroInteractions(errorHandler); // doesn't requires ticket creation
        verify(statusReporter).reportError(cm, e); // event sent to eventing module
        verify(messageQueue).convertAndSend(eq("email_notificator"), any()); // event sent to email-notificator
    }

    private ContainerMessage createContainerMessage() {
        ContainerMessage cm = new ContainerMessage("filename", Endpoint.TEST);
        DocumentInfo di = new DocumentInfo();
        cm.setDocumentInfo(di);

        Route route = new Route();
        route.getEndpoints().add("next");
        route.setDescription("test route");
        assertNotNull(cm.getProcessingInfo());
        cm.getProcessingInfo().setRoute(route);

        return cm;
    }
}
