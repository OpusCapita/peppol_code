package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileConsumer implements Consumer {
    private final String name;
    private final String directory;
    private final String expression;

    public FileConsumer(String name, String directory, String expression) {

        this.name = name;
        this.directory = directory;
        this.expression = expression;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
