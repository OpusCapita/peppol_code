package com.opuscapita.peppol.validator.controller.xsl;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.validator.controller.body.ValidationRule;
import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class ResultParserTest {
    private static ResultParser resultParser;
    private static ContainerMessage cm;

    @Before
    public void before() {
        resultParser = new ResultParser(SAXParserFactory.newInstance());
        resultParser.setCombineThreshold(2);
        cm = new ContainerMessage();
        cm.setDocumentInfo(new DocumentInfo());
        cm.setProcessingInfo(new ProcessingInfo(new Endpoint("test", ProcessType.TEST)));
        cm.getProcessingInfo().setCurrentStatus(new Endpoint("test", ProcessType.TEST), "test");
    }

    @Test
    public void parse() throws Exception {
        try (InputStream inputStream = ResultParserTest.class.getResourceAsStream("/xsl/result/no_error_result.xml")) {
            cm = resultParser.parse(cm, inputStream, new ValidationRule());
        }

        assertTrue(cm.getDocumentInfo().getErrors().isEmpty());
        assertEquals(3, cm.getDocumentInfo().getWarnings().size());
    }

    @Test
    public void testConsolidation() throws Exception {
        try (InputStream inputStream = ResultParserTest.class.getResourceAsStream("/xsl/result/repeatable_warnings.xml")) {
            cm = resultParser.parse(cm, inputStream, new ValidationRule());
        }

        assertEquals(4, cm.getDocumentInfo().getWarnings().size());

        boolean found = false;
        for (DocumentWarning dw : cm.getDocumentInfo().getWarnings()) {
            if (dw.getMessage().contains("(5 SKIPPED)")) {
                if (found) {
                    fail("Skipped message expected only once");
                }
                found = true;
            }
        }
        assertTrue(found);
    }

}