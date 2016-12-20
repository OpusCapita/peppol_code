package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.container.document.impl.ubl.UblDocumentType;
import com.opuscapita.peppol.commons.container.route.Endpoint;
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
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", new Endpoint("test", Endpoint.Type.PEPPOL));

        BaseDocument doc = mock(BaseDocument.class);
        when(doc.getArchetype()).thenReturn(Archetype.UBL);
        when(doc.getDocumentType()).thenReturn(UblDocumentType.ORDER.getTag());
        cm.setBaseDocument(doc);

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
        assertEquals("/result/UBL/Order/test.xml", outputFile);
    }

}
