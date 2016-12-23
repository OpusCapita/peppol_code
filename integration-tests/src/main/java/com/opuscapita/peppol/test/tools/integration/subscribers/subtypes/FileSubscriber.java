package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;

import java.io.File;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileSubscriber extends Subscriber {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileSubscriber.class);
    private final String sourceFile;

    public FileSubscriber(Object timeout, Object sourceFile) {
        super(timeout);
        this.sourceFile = (String) sourceFile;
    }

    @Override
    public List<TestResult> run() {
        File file = null;
        try {
            file = new File(sourceFile);
            if (!file.exists()) {
                logger.error(this.sourceFile + " doesn't exist!");
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceFile, ex);
            return null;
        }
        for (Consumer consumer : consumers) {
            TestResult testResult = consumer.consume(sourceFile);
            testResults.add(testResult);
        }
        return testResults;
    }
}
