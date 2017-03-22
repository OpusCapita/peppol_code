package com.opuscapita.peppol.commons.errors;

import com.opuscapita.commons.servicenow.ServiceNow;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class ErrorHandlerTest {
    private ServiceNow serviceNow = mock(ServiceNow.class);

    @Test
    public void correlationIdDigest() throws Exception {
        String correlationId = "/etc/passwd:NoSuchFileException";
        String digest = new ErrorHandler(serviceNow).correlationIdDigest(correlationId);
        assertTrue(digest.toUpperCase().matches("^[0-9A-F]+$"));
    }

}