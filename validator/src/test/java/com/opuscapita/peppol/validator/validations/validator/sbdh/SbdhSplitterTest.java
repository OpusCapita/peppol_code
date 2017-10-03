package com.opuscapita.peppol.validator.validations.validator.sbdh;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class SbdhSplitterTest {

    @Test
    public void split() throws Exception {
        String sbdh;
        String body;

        try (InputStream inputStream = SbdhSplitterTest.class.getResourceAsStream("/test_data/difi_files/EHF_profile-bii05_invoice_invalid.xml")) {
            SbdhSplitter.Result result = new SbdhSplitter().split(inputStream, "Invoice");
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
