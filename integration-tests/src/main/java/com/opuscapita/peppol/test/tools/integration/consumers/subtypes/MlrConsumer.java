package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MlrConsumer extends FileConsumer {

    private final static Logger logger = LoggerFactory.getLogger(MlrConsumer.class);
    private final String errorDesctiption;

    public MlrConsumer(String id, String fileTestName, String expectedValue, String expectedFile,  Integer delay) {
        super(id, fileTestName, expectedFile, delay);
        this.errorDesctiption = expectedValue;
    }

    @Override
    public TestResult consume(Object consumable) {
        initCurrentDirectory(consumable);

        file = new File(currentDirectory, expectedValue);

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
                if (content.contains(errorDesctiption))
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
