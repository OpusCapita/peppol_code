package com.opuscapita.peppol.test.tools.integration.consumers;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbConsumer implements Consumer {
    private Object expectedValue;

    public DbConsumer(Object expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}