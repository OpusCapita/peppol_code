package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class TemplateToolsTest {

    @Test
    public void templateToPath() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", new Endpoint("test", Endpoint.Type.TEST));
        try (InputStream is = TemplateToolsTest.class.getResourceAsStream("/valid/ehf.xml")) {
            BaseDocument bd = new DocumentLoader().load(is, "/tmp/test.xml");
            cm.setBaseDocument(bd);
        }

        assertEquals("test.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME), cm));
        assertEquals("_test.xml",
                TemplateTools.templateToPath("_" + t(TemplateTools.T_FILENAME), cm));
        assertEquals("test.xmltest.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME) + t(TemplateTools.T_FILENAME), cm));
        assertEquals("test.xml_test.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME) + "_" + t(TemplateTools.T_FILENAME), cm));

        assertEquals("%9908:980361330",
                TemplateTools.templateToPath(t(TemplateTools.T_PERCENT) + t(TemplateTools.T_CUSTOMER_ID), cm));

        assertEquals("UBL/Invoice",
                TemplateTools.templateToPath(t(TemplateTools.T_ARCHETYPE) + "/" + t(TemplateTools.T_DOCUMENT_TYPE), cm));
    }

    private String t(String s) {
        return TemplateTools.BEGIN_END + s + TemplateTools.BEGIN_END;
    }

}
