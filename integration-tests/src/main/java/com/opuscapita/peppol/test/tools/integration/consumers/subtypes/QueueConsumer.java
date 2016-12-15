package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class QueueConsumer extends Consumer {

    private List<String> subscribers;
    private Object expectedValue;

    public QueueConsumer(String id, List<String> subscribers, Object expectedValue) {
        super(id);
        this.subscribers = subscribers;
        this.expectedValue = expectedValue;
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
