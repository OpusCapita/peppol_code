package com.opuscapita.peppol.test.tools.integration.configs;

import com.opuscapita.peppol.test.tools.integration.test.IntegrationTest;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestExecutor {
    List<IntegrationTest> tests = new ArrayList<>();
    ExecutorService executor;
    private final static Logger logger = LoggerFactory.getLogger(IntegrationTestExecutor.class);

    public void addTest(IntegrationTest test) {
        tests.add(test);
    }

    public List<TestResult> runTests() {
        List<TestResult> testResults = new ArrayList<>();

        logger.info("Starting all producers! ");
        tests.forEach(IntegrationTest::runProducers);

        executor = Executors.newFixedThreadPool(tests.size());
        Date start = new Date();
        logger.info("Starting all tests as Runnable via ThreadPool");
        tests.forEach(executor::execute);

        executor.shutdown();

        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Executor finished! in " + (new Date().getTime() - start.getTime()));

        tests.stream().map(IntegrationTest::getTestResults).forEach(testResults::addAll);
        return testResults;
    }
}
