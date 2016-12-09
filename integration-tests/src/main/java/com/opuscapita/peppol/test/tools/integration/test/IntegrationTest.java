package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class IntegrationTest {
    private String name;
    private List<Producer> producers;
    private List<Subscriber> subscribers;
    private List<Consumer> consumers;

    public IntegrationTest(String moduleName, List<Producer> producers, List<Subscriber> subscribers, List<Consumer> consumers) {
        this.name = moduleName;
        this.producers = producers;
        this.subscribers = subscribers;
        this.consumers = consumers;
    }

    public TestResult run() {
        producers.forEach(producer -> producer.run());
        subscribers.forEach(sub -> sub.run());
        return null;
    }
}
