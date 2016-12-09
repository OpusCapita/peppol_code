package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class SeleniumConsumer extends Consumer {
    private Object expectedValue;

    public SeleniumConsumer(String id, Object expectedValue) {
        super(id);
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void consume(String consumable) {

    }
}
