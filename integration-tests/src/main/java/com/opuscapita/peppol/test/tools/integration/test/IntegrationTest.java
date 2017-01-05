package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.16..
 */
public class IntegrationTest {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(IntegrationTest.class);
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

    public List<TestResult> run() {
        logger.info("Test starting: " + name);
        producers.forEach(Producer::run);
        List<TestResult> testResults = new ArrayList<>();
        subscribers.stream().map(Subscriber::run).forEach(testResults::addAll);
        return testResults;
    }
}
