package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class WebWatchDogConsumer extends FileConsumer{
    private final static Logger logger = LoggerFactory.getLogger(WebWatchDogConsumer.class);
    private final String PREFIX = "SEMAIDPOST_PEPPOL_XIB_status_";
    public WebWatchDogConsumer(String id, String name, String expectedValue, Integer delay) {
        super(id, name, expectedValue, delay);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        init(consumable);
        if(!file.exists()) {
            logger.warn("WebWatchDogConsumer: no files to consume in " + file.getAbsolutePath() + " retry in: " + delay);
            waitFixedDelay();
        }

        if(file.exists()) {
            try {
                String[] result = Files.readLines(file, Charsets.UTF_8).get(0).split(";");
                if(result.length < 3) {
                    clean();
                    return new TestResult(name, false, "content length read from WWD report file doesn't match! " + file.getName() + " " + result);
                }
                if(!result[0].contains(expectedValue.replaceAll(PREFIX,""))) {
                    clean();
                    return new TestResult(name, false, expectedValue.replaceAll(PREFIX, "") + " not found in WWD report file: " + file.getName() + " " + result[0]);
                }
                if(!result[1].toUpperCase().equals("OK")) {
                    clean();
                    return new TestResult(name, false, "OK not found in WWD report file: " + file.getName() + " " + result[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
                clean();
                return new TestResult(name, false, e);
            }
            clean();
            return new TestResult(name, true, "Found expected file " + file.getAbsolutePath());
        }
        return result;
    }
}
