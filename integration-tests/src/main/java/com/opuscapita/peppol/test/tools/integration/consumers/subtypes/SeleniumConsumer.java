package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class SeleniumConsumer implements Consumer {
    private Object expectedValue;

    public SeleniumConsumer(Object expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}