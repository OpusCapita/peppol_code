package com.opuscapita.peppol.commons.errors;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class ErrorHandlerTest {
    private ServiceNow serviceNow = mock(ServiceNow.class);
    private ContainerMessageSerializer serializer = mock(ContainerMessageSerializer.class);

    @Test
    public void correlationIdDigest() throws Exception {
        String correlationId = "/etc/passwd:NoSuchFileException";
        String digest = new ErrorHandler(serviceNow, serializer).correlationIdDigest(correlationId);
        assertTrue(digest.toUpperCase().matches("^[0-9A-F]+$"));
    }

    // expect that exception is handled in ErrorHandler and not propagated further
    @Test
    public void testFailure() throws Exception {
        ServiceNowREST rest = mock(ServiceNowREST.class);
        ErrorHandler handler = new ErrorHandler(rest, serializer);

        doThrow(new IOException("bad things happen")).when(rest).insert(any());

        handler.reportWithoutContainerMessage("customer_id", new IllegalArgumentException("exception_message"),
                "short_description", "correlation_id", "file_name");
    }
}