package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.io.File;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class SncSubscriber extends Subscriber {
    private final String sourceDirectory;

    public SncSubscriber(Object timeout, Object sourceDirectory) {
        super(timeout);
        this.sourceDirectory = (String) sourceDirectory;
    }

    @Override
    public List<TestResult> run() {
        if(sourceDirectory == null || sourceDirectory.isEmpty())
            return testResults;
        File directory = new File(sourceDirectory);
        File[] files = directory.listFiles();
        for(Consumer consumer : consumers) {
            TestResult testResult = consumer.consume(files);
            testResults.add(testResult);
        }
        return testResults;
    }
}
