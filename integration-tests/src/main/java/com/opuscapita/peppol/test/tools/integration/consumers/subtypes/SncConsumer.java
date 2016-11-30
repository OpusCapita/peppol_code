package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class SncConsumer implements Consumer {


    private final String name;
    private final String expression;
    private final boolean expected;

    public SncConsumer(String name, String expression, boolean expected) {
        this.name = name;
        this.expression = expression;
        this.expected = expected;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
