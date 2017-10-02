package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class InboundMessageConsumer extends FileConsumer {
    private final static Logger logger = LoggerFactory.getLogger(InboundMessageConsumer.class);

    public InboundMessageConsumer(String id, String name, String expectedValue, Integer delay) {
        super(id, name, expectedValue, delay);
    }

    @Override
    public TestResult consume(Object consumable) {
        if(consumable == null) {
            return new TestResult(name, false, "InboundMessageConsumer: Invalid consumable, null or empty!");
        }
        File directory = (File) consumable;
        logger.info("InboundMessageConsumer: consumable: " + directory.getAbsolutePath() + " length: " + directory.list().length);
        if(!directory.isDirectory()) {
            return new TestResult(name, false, "not a directory " + directory.getAbsolutePath());
        }
        int matchedDirsCount = (int) Arrays.stream(directory.list()).filter(x -> !x.startsWith("9")).count();
        boolean result =  matchedDirsCount == Integer.valueOf(expectedValue);
        return new TestResult(name, result, "Expected value: " + expectedValue + " real: " + matchedDirsCount);
    }
}
