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
        TestResult testResult = null;
        if(consumable == null)
            return new TestResult(name, false, "FileConsumer: Invalid consumable, null or empty!");
        File directory = (File) consumable;
        if(!directory.isDirectory())
            return new TestResult(name, false, "FileConsumer: Directory not found " + directory);
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
            return new TestResult(name, false, "FileConsumer: no files to consume in " + directory);
        }
        for (File f : directory.listFiles()){
            if (f.getName().equals(expectedValue)){
                testResult = new TestResult(name, true, "Found expected file " + f.getAbsolutePath());
                f.delete();
            }
        }
        return testResult;
    }
}
