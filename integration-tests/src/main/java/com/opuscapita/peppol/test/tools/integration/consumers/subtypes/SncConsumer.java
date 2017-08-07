package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class SncConsumer extends FileConsumer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(SncConsumer.class);

    public SncConsumer(String id, String name, String expected) {
        super(id, name, expected);
    }

    @Override
    public TestResult consume(Object consumable) {
        if(consumable == null) {
            return new TestResult(name, false, "SncConsumer: Invalid consumable, null or empty!");
        }

        File directory = (File) consumable;
        if(!directory.isDirectory()) {
            return new TestResult(name, false, "SncConsumer: Directory not found " + directory);
        }

        if(searchforFile(directory))
            return new TestResult(name, true, "Found expected file " + expectedValue);

        logger.warn("SncConsumer: no files to consume in " + file.getAbsolutePath() + " retry in: " + DELAY);
        waitFixedDelay();

        if(searchforFile(directory))  //retry
            return new TestResult(name, true, "Found expected file " + expectedValue);

        return new TestResult(name, false, "not found expected file with name " + expectedValue);
    }

    private boolean searchforFile(File directory){
        for(File f : directory.listFiles()) {
            if(f.getName().startsWith(expectedValue)){
                f.delete();
                return true;
            }
        }
        return false;
    }
}
