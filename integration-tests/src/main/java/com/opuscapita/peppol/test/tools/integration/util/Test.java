package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class Test {
    private final String name;
    private final List<Producer> producers;
    private final List<Subscriber> subscribers;
    private final List<Consumer> consumers;

    public Test(String moduleName, List<Producer> producers, List<Subscriber> subscribers, List<Consumer> consumers) {
        this.name = moduleName;
        this.producers = producers;
        this.subscribers = subscribers;
        this.consumers = consumers;
    }
}
