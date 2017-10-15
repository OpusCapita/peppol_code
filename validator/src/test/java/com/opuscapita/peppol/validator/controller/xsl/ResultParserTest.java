package com.opuscapita.peppol.validator.controller.xsl;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class ResultParserTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void parse() throws Exception {

        ResultParser resultParser = new ResultParser(SAXParserFactory.newInstance());
        ContainerMessage cm = new ContainerMessage();
        cm.setDocumentInfo(new DocumentInfo());
        cm.setProcessingInfo(new ProcessingInfo(new Endpoint("test", ProcessType.TEST), "meatdata"));
        cm.getProcessingInfo().setCurrentStatus(new Endpoint("test", ProcessType.TEST), "test");

        try (InputStream inputStream = ResultParserTest.class.getResourceAsStream("/xsl/result/no_error_result.xml")) {
            cm = resultParser.parse(cm, inputStream);
        }

        assertTrue(cm.getDocumentInfo().getErrors().isEmpty());
        assertEquals(3, cm.getDocumentInfo().getWarnings().size());
    }

}