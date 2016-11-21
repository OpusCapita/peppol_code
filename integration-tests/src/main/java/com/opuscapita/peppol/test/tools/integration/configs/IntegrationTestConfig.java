package com.opuscapita.peppol.test.tools.integration.configs;

import com.opuscapita.peppol.test.tools.integration.test.IntegrationTest;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestConfig {
    List<IntegrationTest> tests = new ArrayList<>();

    public void addTest(IntegrationTest test) {
        tests.add(test);
    }

    public List<TestResult> runTests() {
        return tests.stream().map(IntegrationTest::run).collect(Collectors.toCollection(ArrayList::new));
    }
}
