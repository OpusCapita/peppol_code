package com.opuscapita.peppol.test.tools.smoke.checks;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class CheckResult {
    final String name;
    final boolean passed;
    final String details;
    final Map<String, Object> rawConfigOfCheck;

    public CheckResult(String name, boolean passed, String details, Map<String, Object> rawConfigOfCheck) {
        this.name = name;
        this.passed = passed;
        this.details = details;
        this.rawConfigOfCheck = rawConfigOfCheck;
    }

    public String getName() {
        return name;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "name='" + name + '\'' +
                ", passed=" + passed +
                ", details='" + details + '\'' +
                '}';
    }
}
