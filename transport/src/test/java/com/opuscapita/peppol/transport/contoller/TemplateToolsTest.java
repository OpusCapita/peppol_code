package com.opuscapita.peppol.transport.contoller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.junit.Test;

import java.io.InputStream;

import static com.opuscapita.peppol.transport.contoller.TemplateTools.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class TemplateToolsTest {

    @Test
    public void templateToPath() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", Endpoint.GATEWAY);
        try (InputStream is = TemplateToolsTest.class.getResourceAsStream("/valid/ehf.xml")) {
            BaseDocument bd = new DocumentLoader().load(is, "/tmp/test.xml");
            cm.setBaseDocument(bd);
        }

        assertEquals("test.xml", TemplateTools.templateToPath(t(T_FILENAME), cm));
        assertEquals("_test.xml", TemplateTools.templateToPath("_" + t(T_FILENAME), cm));
        assertEquals("test.xmltest.xml", TemplateTools.templateToPath(t(T_FILENAME) + t(T_FILENAME), cm));
        assertEquals("test.xml_test.xml", TemplateTools.templateToPath(t(T_FILENAME) + "_" + t(T_FILENAME), cm));

        assertEquals("%9908:980361330", TemplateTools.templateToPath(t(T_PERCENT) + t(T_CUSTOMER_ID), cm));

        assertEquals("UBL/Invoice", TemplateTools.templateToPath(t(T_ARCHETYPE) + "/" + t(T_DOCUMENT_TYPE), cm));
    }

    private String t(String s) {
        return BEGIN_END + s + BEGIN_END;
    }

}
