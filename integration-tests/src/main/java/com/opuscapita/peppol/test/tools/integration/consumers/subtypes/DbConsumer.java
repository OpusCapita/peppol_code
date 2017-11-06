package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbConsumer extends Consumer {
    private final String name;
    private String expectedValue;

    public DbConsumer(String id, String consumerName, Object expectedValue) {
        super(id);
        this.name = consumerName;
        this.expectedValue = String.valueOf(expectedValue);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        boolean passed = expectedValue.equals(String.valueOf(consumable));
        String details = (passed) ? "successfully fetched data from DB!" :
                "Got value " + consumable + " but expected: " + expectedValue;
        return new TestResult(name,passed,details);
    }
}
