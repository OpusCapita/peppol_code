package com.opuscapita.peppol.commons.container.xml;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class DocumentTemplatesTest {
    private ErrorHandler errorHandler = mock(ErrorHandler.class);

    @Test
    public void testInitialization() throws Exception {
        DocumentTemplates it = new DocumentTemplates(errorHandler, new Gson());

        DocumentTemplate invoice =
                it.getTemplates().stream().filter(dt -> "PEPPOL_BIS.Invoice".equals(dt.getName())).findFirst().orElse(null);
        assertNotNull(invoice);
        assertEquals(12, invoice.getFields().size());
    }

}