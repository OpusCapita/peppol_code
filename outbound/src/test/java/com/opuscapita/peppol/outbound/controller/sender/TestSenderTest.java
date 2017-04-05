package com.opuscapita.peppol.outbound.controller.sender;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.commons.container.xml.JsonDocumentTemplates;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class TestSenderTest {
    private DocumentParser parser;
    private ErrorHandler errorHandler = mock(ErrorHandler.class);

    public TestSenderTest() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        DocumentTemplates templates = new JsonDocumentTemplates(errorHandler, new Gson());
        parser = new DocumentParser(saxParser, templates);
    }

    @Test
    public void getUpdatedFileContent() throws Exception {
        File tempFile = File.createTempFile("unit-test.", ".temp");
        tempFile.deleteOnExit();

        try (InputStream is = TestSenderTest.class.getResourceAsStream("/Valid1.xml")) {
            IOUtils.copy(is, new FileOutputStream(tempFile));
        }
        Map<String, Integer> expected = new HashMap<>();

        expected.put("TEST_ID", 0);
        expected.put("9908:985853304", 1);
        expected.put("9908:929120825", 2);
        try (InputStream is = TestSenderTest.class.getResourceAsStream("/Valid1.xml")) {
            checkOccurrences(is, expected);
        }

        ContainerMessage cm = new ContainerMessage(
                "test", tempFile.getAbsolutePath(), new Endpoint("test", ProcessType.TEST));
        cm.setDocumentInfo(new DocumentLoader(parser).load(tempFile, new Endpoint("test", ProcessType.TEST)));

        TestSender testSender = new TestSender(null, new FakeSender());
        InputStream result = testSender.getUpdatedFileContent(cm, "TEST_ID");

        expected.put("TEST_ID", 3);
        expected.put("9908:985853304", 0);
        expected.put("9908:929120825", 0);
        checkOccurrences(result, expected);

    }

    private void checkOccurrences(InputStream inputStream, Map<String, Integer> expected) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while(true) {
            line = in.readLine();
            if (line == null) {
                break;
            }

            for (String key : expected.keySet()) {
                int occurrences = StringUtils.countOccurrencesOf(line, key);
                Integer value = expected.get(key) - occurrences;
                expected.put(key, value);
            }
        }

        for (String key : expected.keySet()) {
            int value = expected.get(key);
            assertEquals("Failed on " + key, 0, value);
        }
    }

}