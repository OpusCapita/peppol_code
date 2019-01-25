package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
public class TransportControllerTest {
    private String inputFile;
    private String outputFile;

    @Test
    public void storeMessage() throws Exception {
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            return;
        }
        ContainerMessage cm = new ContainerMessage("/tmp/test.xml", Endpoint.TEST);

        DocumentInfo doc = mock(DocumentInfo.class);
        when(doc.getArchetype()).thenReturn(Archetype.PEPPOL_BIS);
        when(doc.getDocumentType()).thenReturn("Order");
        cm.setDocumentInfo(doc);

        TransportController controller = new TransportController() {
            @Override
            protected void copyFile(@NotNull File input, @NotNull File output) throws IOException {
                inputFile = input.getAbsolutePath();
                outputFile = output.getAbsolutePath();
            }
        };
        controller.setDirectory("/result");
        controller.setTemplate("%ARCHETYPE%/%DOCUMENT_TYPE%/%FILENAME%");

        controller.storeMessage(cm);

        assertEquals("/tmp/test.xml", inputFile);
        assertEquals("/result/PEPPOL_BIS/Order/test.xml", outputFile);
    }

}
