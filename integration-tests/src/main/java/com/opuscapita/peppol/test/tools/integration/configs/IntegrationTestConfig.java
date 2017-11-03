package com.opuscapita.peppol.test.tools.integration.configs;

import com.opuscapita.peppol.test.tools.integration.test.IntegrationTest;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestConfig {
    List<IntegrationTest> tests = new ArrayList<>();

    public void addTest(IntegrationTest test) {
        tests.add(test);
    }

    public List<TestResult> runTests() {
        List <TestResult> testResults = new ArrayList<>();
        tests.forEach(IntegrationTest::runProducers);
        tests.stream().map(IntegrationTest::runTest).forEach(testResults::addAll);
        return testResults;
    }
}
