package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class TemplateToolsTest {
    @Test
    public void templateToPath() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", Endpoint.TEST);

        DocumentInfo di = new DocumentInfo();
        di.setSenderId("SENDER_ID");
        di.setArchetype(Archetype.EHF);
        di.setDocumentType("Invoice");
        cm.setDocumentInfo(di);

        assertEquals("test.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME), cm));
        assertEquals("_test.xml",
                TemplateTools.templateToPath("_" + t(TemplateTools.T_FILENAME), cm));
        assertEquals("test.xmltest.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME) + t(TemplateTools.T_FILENAME), cm));
        assertEquals("test.xml_test.xml",
                TemplateTools.templateToPath(t(TemplateTools.T_FILENAME) + "_" + t(TemplateTools.T_FILENAME), cm));
        assertEquals("%SENDER_ID",
                TemplateTools.templateToPath(t(TemplateTools.T_PERCENT) + t(TemplateTools.T_CUSTOMER_ID), cm));
        assertEquals("EHF/Invoice",
                TemplateTools.templateToPath(t(TemplateTools.T_ARCHETYPE) + "/" + t(TemplateTools.T_DOCUMENT_TYPE), cm));
    }

    private String t(String s) {
        return TemplateTools.BEGIN_END + s + TemplateTools.BEGIN_END;
    }

}
