package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileConsumer extends Consumer {
    private final String name;
    private final String directory;
    private final String expression;

    public FileConsumer(String id, String name, String directory, String expression) {
        super(id);
        this.name = name;
        this.directory = directory;
        this.expression = expression;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {

        return null;
    }
}
