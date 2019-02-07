package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class TestSenderTest {
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

        ContainerMessage cm = new ContainerMessage(tempFile.getAbsolutePath(), Endpoint.TEST);
        DocumentInfo di = new DocumentInfo();
        di.setSenderId("9908:985853304");
        di.setRecipientId("9908:929120825");
        cm.setDocumentInfo(di);

        TestSender testSender = new TestSender(new FakeSender());
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
