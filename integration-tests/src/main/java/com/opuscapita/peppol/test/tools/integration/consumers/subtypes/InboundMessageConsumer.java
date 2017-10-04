package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InboundMessageConsumer extends FileConsumer {
    private final static Logger logger = LoggerFactory.getLogger(InboundMessageConsumer.class);

    public InboundMessageConsumer(String id, String name, String expectedValue, Integer delay) {
        super(id, name, expectedValue, delay);
    }

    @Override
    public TestResult consume(Object consumable) {
        if (consumable == null) {
            return new TestResult(name, false, "InboundMessageConsumer: Invalid consumable, null or empty!");
        }
        File tempDir = (File) consumable;
        if (!tempDir.isDirectory()) {
            return new TestResult(name, false, "not a directory " + tempDir.getAbsolutePath());
        }
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File fullDir = new File(tempDir, date);
        if (!fullDir.isDirectory()) {
            return new TestResult(name, false, "not a directory " + fullDir.getAbsolutePath());
        }

        logger.info("InboundMessageConsumer: consumable: " + tempDir.getAbsolutePath() + " length: " + fullDir.list().length);

        boolean result = Integer.valueOf(expectedValue) == fullDir.list().length;
        return new TestResult(name, result, "Expected value: " + expectedValue + " real: " + fullDir.list().length);
    }
}
