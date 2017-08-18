package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileConsumer extends Consumer {
    private final static Logger logger = LoggerFactory.getLogger(FileConsumer.class);
    protected final String name;
    protected final String expectedValue;
    protected final static int DELAY = 12000;
    protected File file;
    protected TestResult result;

    public FileConsumer(String id, String name, String expectedValue) {
        super(id);
        this.name = name;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        init(consumable);

        if(!file.exists()) {
            logger.warn("FileConsumer: no files to consume in " + file.getAbsolutePath() + " retry in: " + DELAY);
            waitFixedDelay();
        }


            if(file.exists()) {
            clean();
            return new TestResult(name, true, "Found expected file " + file.getAbsolutePath());
        }
        return result;
    }

    protected void init(Object consumable) {
        if(consumable == null) {
            result = new TestResult(name, false, "FileConsumer: Invalid consumable, null or empty!");
            return;
        }

        File directory = (File) consumable;
        if(!directory.isDirectory()) {
            result = new TestResult(name, false, "FileConsumer: Directory not found " + directory);
            return;
        }

        result = new TestResult(name, false, "FileConsumer: expected file: " + expectedValue + " not found in: " + directory);
        file = new File(directory,expectedValue);
    }

    protected boolean clean(){
        return file.delete();
    }

    protected void waitFixedDelay() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
