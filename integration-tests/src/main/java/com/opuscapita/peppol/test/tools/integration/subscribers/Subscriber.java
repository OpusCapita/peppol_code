package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.subtypes.DbSubscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public abstract class Subscriber {
    protected List<Consumer> consumers = new ArrayList<>();
    protected Object consumable;
    private final static Logger logger = LoggerFactory.getLogger(DbSubscriber.class);
    private final static int RETRIES = 30;
    private List<TestResult> testResults = new ArrayList<>();
    /* in millis*/
    protected int timeout;

    public Subscriber(Object timeout) {
        if (timeout != null)
            this.timeout = (int) timeout;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public List<TestResult> run() {
        logger.info(this.getClass().getName() + ": started!");
        try {
            for (int i = 0; i < RETRIES; i++) {
                fetchConsumable();
                if (consumable == null) {
                    logger.info(this.getClass().getName() + ": got no result, retrying in " + timeout);
                    Thread.sleep(timeout);
                } else {
                    logger.info(this.getClass().getName() + ": got the result" + consumable);
                    for (Consumer consumer : consumers) {
                        TestResult testResult = consumer.consume(consumable);
                        testResults.add(testResult);
                        return testResults;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return testResults;
    }

    protected abstract void fetchConsumable();
}
