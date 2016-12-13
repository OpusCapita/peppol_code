package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbConsumer extends Consumer {
    private final String name;
    private Object expectedValue;

    public DbConsumer(String id, String consumerName, Object expectedValue) {
        super(id);
        this.name = consumerName;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void consume(String consumable) {
        boolean result = Integer.valueOf(consumable).equals((Integer)expectedValue);
        String test = "";
    }
}
