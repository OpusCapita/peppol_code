package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileConsumer extends Consumer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileConsumer.class);
    private final String name;
    private final String expectedValue;
    private final static int DELAY = 4000;

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
        if(consumable == null)
            return new TestResult(name, false, "FileConsumer: Invalid consumable, null or empty!");
        File directory = (File) consumable;
        if(!directory.isDirectory())
            return new TestResult(name, false, "FileConsumer: Directory not found " + directory);
        TestResult testResult = new TestResult(name, false, "FileConsumer: expected file: " + expectedValue + " not found in: " + directory);
        if(directory.list().length < 1){
            logger.warn("FileConsumer: no files to consume in " + directory + " retry in: " + DELAY);
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(directory.list().length < 1) {
            logger.warn("FileConsumer: still no files to consume in " + directory);
            return testResult;
        }
        for (File f : directory.listFiles()){
            if (f.getName().equals(expectedValue)){
                f.delete();
                return new TestResult(name, true, "Found expected file " + f.getAbsolutePath());
            }
        }
        return testResult;
    }
}
