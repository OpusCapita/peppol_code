package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreprocessingFileConsumer extends FileConsumer {
    private final static Logger logger = LoggerFactory.getLogger(PreprocessingFileConsumer.class);

    public PreprocessingFileConsumer(String id, String testName, String expectedValue, Integer delay) {
        super(id, testName, expectedValue, delay);
    }

    @Override
    public TestResult consume(Object consumable) {
        initCurrentDirectory(consumable);
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        logger.info("expected value: " + expectedValue );
        String fileToSearch = expectedValue.replace("$date", date);
        file = new File(currentDirectory, fileToSearch);
        if (!file.exists()) {
            return new TestResult(name, false, "Expected file not found:  " + file.getAbsolutePath());
        }
        clean();
        return new TestResult(name, true, "Found expected file:  " + file.getAbsolutePath());
    }
}
