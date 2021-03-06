package com.opuscapita.peppol.test.tools.integration.test;

/**
 * Created by gamanse1 on 2016.11.21..
 */
public class TestResult {
    final String name;
    final boolean passed;
    final String details;

    public TestResult() {
        this.name = "empty";
        this.passed = false;
        this. details = "empty";
    }

    public TestResult(String name, boolean passed, String details) {
        this.name = name;
        this.passed = passed;
        this.details = details;
    }

    public TestResult(String name, boolean passed, Exception e) {
        this(name,passed,e.getMessage());
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public boolean isPassed() {
        return passed;
    }

    @Override
    public String toString() {
        return "Test: " + name + " " + passed + " Details: " + details;
    }
}
