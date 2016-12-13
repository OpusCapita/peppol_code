package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class SncSubscriber extends Subscriber {
    public SncSubscriber(Object timeout) {
        super(timeout);
    }

    @Override
    public List<TestResult> run() {

        return testResults;
    }
}
