package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class SncConsumer extends Consumer {


    private final String name;
    private final String expression;
    private final boolean expected;

    public SncConsumer(String id, String name, String expression, boolean expected) {
        super(id);
        this.name = name;
        this.expression = expression;
        this.expected = expected;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(String consumable) {

        return null;
    }
}
