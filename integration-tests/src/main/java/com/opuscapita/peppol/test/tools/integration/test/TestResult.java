package com.opuscapita.peppol.test.tools.integration.test;

/**
 * Created by gamanse1 on 2016.11.21..
 */
public class TestResult {
    final String name;
    final boolean passed;
    final String details;

    public TestResult(String name, boolean passed, String details) {
        this.name = name;
        this.passed = passed;
        this.details = details;
    }
}
