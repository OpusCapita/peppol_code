package com.opuscapita.peppol.test.tools.integration.test;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by gamanse1 on 2016.11.16..
 */
public class IntegrationTest implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private String name;
    private List<Producer> producers;
    private List<Subscriber> subscribers;
    private List<TestResult> testResults = new ArrayList<>();

    public IntegrationTest(String moduleName, List<Producer> producers, List<Subscriber> subscribers) {
        this.name = moduleName;
        this.producers = producers;
        this.subscribers = subscribers;
    }

    public void runProducers() {
        logger.info("**** Producer starting for: " + name +" ****");
        producers.forEach(Producer::run);
    }

    @Override
    public void run() {
        logger.info("**** Test starting: " + name +" ****");
        subscribers.stream().map(Subscriber::run).forEach(testResults::addAll);
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}
