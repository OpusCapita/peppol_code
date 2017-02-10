package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public abstract class Subscriber {
    protected List<Consumer> consumers = new ArrayList<>();
    protected List<TestResult> testResults = new ArrayList<>();
    /* in millis*/
    protected int timeout;

    public Subscriber(Object timeout) {
        if(timeout != null)
            this.timeout = (int)timeout;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public abstract List<TestResult> run();
}
