package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MlrConsumer extends FileConsumer {

    private final static Logger logger = LoggerFactory.getLogger(MlrConsumer.class);
    private final String ERROR_DESCRIPTION = "<cbc:Description>OTHER_ERROR: This sending expected to fail I/O in test mode</cbc:Description>";

    public MlrConsumer(String id, String fileTestName, String expectedValue, Integer delay) {
        super(id, fileTestName, expectedValue, delay);
    }

    @Override
    public TestResult consume(Object consumable) {
        init(consumable);
        if (file == null) {
            return result;
        }
        if (!file.exists()) {
            logger.warn("MlrConsumer: no files to consume in " + file.getAbsolutePath() + " retry in: " + delay);
            waitFixedDelay();
        }
        try {
            if (file.exists()) {
                String content = Files.toString(file, Charsets.UTF_8);
                logger.info("mlr content found: " + content);
                if (content.contains(ERROR_DESCRIPTION))
                    result = new TestResult(name, true, "IO exception found in " + file.getAbsolutePath());
                else
                    result = new TestResult(name, false, "IO exception not found in " + file.getAbsolutePath());
                clean();
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
