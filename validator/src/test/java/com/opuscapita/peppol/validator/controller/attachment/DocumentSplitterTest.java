package com.opuscapita.peppol.validator.controller.attachment;

import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;

import static com.opuscapita.peppol.validator.controller.attachment.DocumentSplitter.MINIMAL_PDF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class DocumentSplitterTest {

    @Test
    public void split() throws Exception {
        String sbdh;
        String body;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

        try (InputStream inputStream = DocumentSplitterTest.class.getResourceAsStream("/test_data/difi_files/EHF_profile-bii05_invoice_invalid.xml")) {
            DocumentSplitterResult result = new DocumentSplitter(xmlInputFactory, mock(AttachmentValidator.class)).split(inputStream, "Invoice");
            sbdh = new String(result.getSbdh());
            body = new String(result.getDocumentBody());
        }

        assertTrue(sbdh.length() > 0);
        assertTrue(sbdh.startsWith("<StandardBusinessDocument"));
        assertTrue(sbdh.contains("<StandardBusinessDocumentHeader>"));
        assertTrue(sbdh.contains("</StandardBusinessDocumentHeader>"));
        assertTrue(sbdh.trim().endsWith("</StandardBusinessDocument>"));
        assertFalse(sbdh.contains("<Invoice"));
        assertFalse(sbdh.contains("</Invoice>"));
        assertFalse(sbdh.contains("Attachment"));

//        System.out.println(body);

        assertTrue(body.length() > 0);
        assertTrue(body.startsWith("<Invoice"));
        assertTrue(body.trim().endsWith("</Invoice>"));
        assertFalse(body.contains("<StandardBusinessDocument"));
        assertFalse(body.contains("<StandardBusinessDocumentHeader>"));
        assertFalse(body.contains("</StandardBusinessDocumentHeader>"));
        assertFalse(body.contains("</StandardBusinessDocument>"));
        assertTrue(body.contains("mimeCode=\"application/pdf\">" + MINIMAL_PDF + "<"));
    }

}
