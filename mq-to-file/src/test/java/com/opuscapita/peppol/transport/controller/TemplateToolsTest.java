package com.opuscapita.peppol.transport.controller;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class TemplateToolsTest {
    private DocumentParser parser;

    public TemplateToolsTest() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        ErrorHandler errorHandler = mock(ErrorHandler.class);
        DocumentTemplates templates = new DocumentTemplates(errorHandler, new Gson());
        parser = new DocumentParser(factory.newSAXParser(), templates);
    }

    @Test
    public void templateToPath() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", new Endpoint("test", ProcessType.TEST));
        try (InputStream is = TemplateToolsTest.class.getResourceAsStream("/valid/ehf.xml")) {
            DocumentInfo di = new DocumentLoader(parser).load(is, "/tmp/test.xml", new Endpoint("test", ProcessType.TEST));
            cm.setDocumentInfo(di);
            di.getErrors().forEach(System.out::println);
            assertEquals(Archetype.EHF, di.getArchetype());
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
        assertEquals("EHF/Invoice",
                TemplateTools.templateToPath(t(TemplateTools.T_ARCHETYPE) + "/" + t(TemplateTools.T_DOCUMENT_TYPE), cm));
    }

    private String t(String s) {
        return TemplateTools.BEGIN_END + s + TemplateTools.BEGIN_END;
    }

}
