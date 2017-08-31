package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.eventing.destinations.mlr.MessageLevelResponseCreator;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class MessageLevelResponseReporterTest {
    private Storage storage = mock(Storage.class);

    @Test
    public void testIgnoreInbound() throws Exception {
        Endpoint ep = new Endpoint("in", ProcessType.IN_INBOUND);
        ContainerMessage cm = new ContainerMessage("metadata", "test.txt", ep);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setArchetype(Archetype.EHF);
        ProcessingInfo pi = new ProcessingInfo(ep, "metadata").setCurrentStatus(ep, "ERROR");
        cm.setProcessingInfo(pi);

        MessageLevelResponseCreator creator = mock(MessageLevelResponseCreator.class);

        MessageLevelResponseReporter reporter = new MessageLevelResponseReporter(creator, storage);
        reporter.process(cm);
        verify(creator, never()).reportError(any());
        verify(creator, never()).reportSuccess(any());
    }

    @Test
    public void testProcessError() throws Exception {
        Endpoint ep = new Endpoint("xxx", ProcessType.OUT_PEPPOL);
        ContainerMessage cm = new ContainerMessage("metadata", "test.txt", ep);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setArchetype(Archetype.INVALID);
        ProcessingInfo pi = new ProcessingInfo(ep, "metadata").setCurrentStatus(ep, "ERROR");
        cm.setProcessingInfo(pi);

        MessageLevelResponseCreator creator = mock(MessageLevelResponseCreator.class);
        when(creator.reportError(any())).thenReturn(new ApplicationResponseType());

        MessageLevelResponseReporter reporter = new MessageLevelResponseReporter(creator, storage);
        reporter.process(cm);
        verify(creator).reportError(cm);
        verify(creator, never()).reportSuccess(any());
    }

    @Test
    public void testProcessSuccess() throws Exception {
        Endpoint ep = new Endpoint("xxx", ProcessType.OUT_OUTBOUND);
        ContainerMessage cm = new ContainerMessage("metadata", "test.txt", ep);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setArchetype(Archetype.EHF);
        ProcessingInfo pi = new ProcessingInfo(ep, "metadata").setCurrentStatus(ep, "ERROR");
        cm.setProcessingInfo(pi);
        pi.setTransactionId("transaction id");

        MessageLevelResponseCreator creator = mock(MessageLevelResponseCreator.class);
        when(creator.reportSuccess(any())).thenReturn(new ApplicationResponseType());

        MessageLevelResponseReporter reporter = new MessageLevelResponseReporter(creator, storage);
        reporter.process(cm);
        verify(creator, never()).reportError(any());
        verify(creator).reportSuccess(cm);
    }

    @Test
    public void testProcessInTheMiddle() throws Exception {
        Endpoint ep = new Endpoint("xxx", ProcessType.OUT_PEPPOL);
        ContainerMessage cm = new ContainerMessage("metadata", "test.txt", ep);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setArchetype(Archetype.EHF);
        ProcessingInfo pi = new ProcessingInfo(ep, "metadata").setCurrentStatus(ep, "ERROR");
        cm.setProcessingInfo(pi);

        MessageLevelResponseCreator creator = mock(MessageLevelResponseCreator.class);

        MessageLevelResponseReporter reporter = new MessageLevelResponseReporter(creator, storage);
        reporter.process(cm);
        verify(creator, never()).reportError(any());
        verify(creator, never()).reportSuccess(any());
    }

    @Test
    public void testProcessBadFormed() throws Exception {
        Endpoint ep = new Endpoint("xxx", ProcessType.OUT_PEPPOL_FINAL);
        ContainerMessage cm = new ContainerMessage("metadata", "test.txt", ep);
        MessageLevelResponseCreator creator = mock(MessageLevelResponseCreator.class);

        MessageLevelResponseReporter reporter = new MessageLevelResponseReporter(creator, storage);
        reporter.process(cm);
        verify(creator, never()).reportError(any());
        verify(creator, never()).reportSuccess(any());
    }
}