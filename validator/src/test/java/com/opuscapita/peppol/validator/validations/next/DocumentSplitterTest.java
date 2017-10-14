package com.opuscapita.peppol.validator.validations.next;

import com.opuscapita.peppol.validator.controller.util.DocumentSplitter;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
            DocumentSplitter.Result result = new DocumentSplitter(xmlInputFactory).split(inputStream, "Invoice");
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

        assertTrue(body.length() > 0);
        assertTrue(body.startsWith("<Invoice"));
        assertTrue(body.trim().endsWith("</Invoice>"));
        assertFalse(body.contains("<StandardBusinessDocument"));
        assertFalse(body.contains("<StandardBusinessDocumentHeader>"));
        assertFalse(body.contains("</StandardBusinessDocumentHeader>"));
        assertFalse(body.contains("</StandardBusinessDocument>"));
        assertFalse(body.contains("Attachment"));
    }

}
