package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class QueueConsumer implements Consumer {

    private List<String> subscribers;
    private Object expectedValue;

    public QueueConsumer(List<String> subscribers, Object expectedValue) {
        this.subscribers = subscribers;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
