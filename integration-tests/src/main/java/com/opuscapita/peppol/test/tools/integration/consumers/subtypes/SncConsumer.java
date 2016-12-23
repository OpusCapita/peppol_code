package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class SncConsumer extends Consumer {


    private final String name;
    private final String expected;

    public SncConsumer(String id, String name, String expected) {
        super(id);
        this.name = name;
        this.expected = expected;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        if(consumable == null)
            return new TestResult(name, false, "SncConsumer: Invalid consumable, null or empty!");
        File directory = (File) consumable;
        if(!directory.isDirectory() || directory.list().length < 1)
            return new TestResult(name, false, "SncConsumer: no files to consume in: " + directory);
        //TODO: continue with fileName == expected
        //Mock
        return new TestResult();
    }
}
