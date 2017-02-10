package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class MqConsumer extends Consumer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(MqConsumer.class);
    private List<String> subscribers;
    private int expectedValue;
    private final String name;

    public MqConsumer(String id,  String name, List<String> subscribers, Object expectedValue) {
        super(id);
        this.name = name;
        this.subscribers = subscribers;
        this.expectedValue = (int) expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        List<Object> messages = (List<Object>) consumable;
        logger.info("MqConsumer: " + name + " starting!");
        boolean  success = (expectedValue == messages.size());
        return new TestResult(name, success, "expected message count: " + expectedValue + " got messages: " + messages.size());
    }
}