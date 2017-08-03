package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class SncConsumer extends FileConsumer {

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

        for(File f : directory.listFiles()) {
            if(f.getName().startsWith(expectedValue)){
                f.delete();
                return new TestResult(name, true, "Found expected file " + f.getAbsolutePath());
            }
        }

        return new TestResult(name, false, "not found expected file with name " + expectedValue);
    }
}
